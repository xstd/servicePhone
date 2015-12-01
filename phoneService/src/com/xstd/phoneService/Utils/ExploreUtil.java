package com.xstd.phoneService.Utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.common.utils.GsonUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.plugin.internet.InternetUtils;
import com.xstd.phoneService.Config;
import com.xstd.phoneService.api.IMSIUpdateRequest;
import com.xstd.phoneService.api.IMSIUpdateResponse;
import com.xstd.phoneService.model.receive.SMSReceived;
import com.xstd.phoneService.model.receive.SMSReceivedDao;
import com.xstd.phoneService.model.update.SMSUpdateSyncStatus;
import com.xstd.phoneService.model.update.SMSUpdateSyncStatusDao;
import com.xstd.phoneService.setting.SettingManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by michael on 13-12-11.
 */
public class ExploreUtil {

    private static final String DEBUG_DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";

    public static String formatTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEBUG_DATE_FORMAT);
        return dateFormat.format(time);
    }

//    public static void syncUpdateIMSI2Phone1(final Context context, final SMSReceivedDao receivedDao
//                                                , final Handler handler, final long lastUpdateTime) {
//        CustomThreadPool.asyncWork(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    List<SMSReceived> data = lastUpdateTime > 0
//                                                 ? (receivedDao.queryBuilder().orderDesc(SMSReceivedDao.Properties.ReceiveTime)
//                                                        .where(SMSReceivedDao.Properties.ReceiveTime.ge(lastUpdateTime)).list())
//                                                 : receivedDao.loadAll();
//                    HashMap<String, String> update = new HashMap<String, String>();
//                    for (SMSReceived smsReceived : data) {
//                        update.put(smsReceived.getImei(), smsReceived.getFrom());
//                    }
//                    upateIMSI2Service(context, update, handler);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Config.LOGD("[[syncUpdateIMSI2Phone]]", e);
//                }
//            }
//        });
//    }

    public static void syncUpdateIMSI2Phone(final Context context, final SMSReceivedDao receivedDao
                                               , final Handler handler, final int lastUpdateCount) {
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    long lastUpdateTime = SettingManager.getInstance().getLastUpdateSMSReceivedTime();
                    List<SMSReceived> data = null;
                    if (lastUpdateTime == 0) {
                        //如果从来都没有上传过
                        data = lastUpdateCount > 0
                                   ? (receivedDao.queryBuilder().orderDesc(SMSReceivedDao.Properties.ReceiveTime)
                                          .limit(lastUpdateCount + 200).list())
                                   : receivedDao.loadAll();
                    } else {
                        data = receivedDao.queryBuilder().orderDesc(SMSReceivedDao.Properties.ReceiveTime)
                                   .where(SMSReceivedDao.Properties.ReceiveTime.ge(lastUpdateTime)).list();
                    }

                    HashMap<String, String> update = new HashMap<String, String>();
                    for (SMSReceived smsReceived : data) {
                        update.put(smsReceived.getImei(), smsReceived.getFrom());
                    }
                    upateIMSI2Service(context, update, handler, (data.size() > 0 ? data.get(0).getReceiveTime() : lastUpdateTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Config.LOGD("[[syncUpdateIMSI2Phone]]", e);
                }
            }
        });
    }

    private static void upateIMSI2Service(final Context context, HashMap<String, String> data, final Handler handler, long lastUpdateSMSTime) {
        try {
            String imsi = UtilsRuntime.getIMSI(context);
            UUID uuid = UUIDUtils.deviceUuidFactory(context);
            String unique = null;
            if (uuid != null) {
                unique = uuid.toString();
            } else {
                unique = imsi;
                UUIDUtils.saveUUID(context, unique);
            }

            String DATE_FORMAT = "yyyy-MM-dd";
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            String time = dateFormat.format(System.currentTimeMillis());

            String phoneType = Build.MODEL;
            if (TextUtils.isEmpty(phoneType)) {
                phoneType = "Android";
            }
            phoneType = phoneType.replace(" ", "");

            String dataUpdate = GsonUtils.toJson(data);
            IMSIUpdateRequest request = new IMSIUpdateRequest(dataUpdate, unique, time, phoneType, true);
            final IMSIUpdateResponse response = InternetUtils.request(context, request);
            if (response != null && response.result != -1) {
                SettingManager.getInstance().setLastUpdateSMSReceivedTime(lastUpdateSMSTime);
                SettingManager.getInstance().setLastSyncTime(System.currentTimeMillis());

                SMSUpdateSyncStatusDao updateDao = UpateSyncDaoUtils.getDaoSessionForUpdate(context).getSMSUpdateSyncStatusDao();
                SMSUpdateSyncStatus updateObj = new SMSUpdateSyncStatus();
                updateObj.setUpdateTime(lastUpdateSMSTime);
                updateDao.insert(updateObj);

                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int curtotal = response.result;
                            int upadteCount = response.uploadCont;
                            int orgCount = response.orgCount;
                            String show = "服务器现有:[" + curtotal + "]项，此次上传数据更新过了:[" + (curtotal - orgCount) + "]项, 此次实际上传了:["
                                              + upadteCount + "]";
                            Toast.makeText(context,
                                              show,
                                              Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Config.LOGD("[[syncUpdateIMSI2Phone]]", e);
        }

        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "上传失败，请重新上传", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static boolean explore(String targetFileFullPath, SMSReceivedDao receivedDao) {
        if (TextUtils.isEmpty(targetFileFullPath) || receivedDao == null) return false;

        try {
            List<SMSReceived> data = receivedDao.loadAll();
            if (data != null && data.size() > 0) {
                Properties p = new Properties();
                for (SMSReceived item : data) {
                    p.put(item.getImei(), item.getFrom());
                }
                FileOutputStream out = new FileOutputStream(new File(targetFileFullPath));
                OutputStreamWriter w = new OutputStreamWriter(out, "utf-8");
                p.store(w, null);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<SMSReceived> exploreBackupData(String targetFileFullPath, SMSReceivedDao receivedDao) {
        if (TextUtils.isEmpty(targetFileFullPath) || receivedDao == null) return null;

        try {
            List<SMSReceived> data = receivedDao.queryBuilder().orderDesc(SMSReceivedDao.Properties.ReceiveTime).build().list();
            if (data != null && data.size() > 0) {
                FileWriter writer = new FileWriter(targetFileFullPath);
                for (SMSReceived info : data) {
                    writer.write(formatTime(info.getReceiveTime()) + "=" + info.getFrom() + "=" + info.getImei() + "=" + info.getNetworkType() + "="
                                     + info.getPhoneType() + "=" + String.valueOf(info.getReceiveTime()));
                    writer.write("\n");
                }
                writer.close();

                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean exportDetail(String targetFileFullPath, SMSReceivedDao receivedDao) {
        if (TextUtils.isEmpty(targetFileFullPath) || receivedDao == null) return false;

        try {
            List<SMSReceived> data = receivedDao.loadAll();
            if (data != null && data.size() > 0) {
                Properties p = new Properties();
                for (SMSReceived item : data) {
                    String info = item.getImei() + ";" + item.getNetworkType() + ";" + item.getPhoneType();
                    p.put(item.getFrom(), info);
                }
                FileOutputStream out = new FileOutputStream(new File(targetFileFullPath));
                OutputStreamWriter w = new OutputStreamWriter(out, "utf-8");
                p.store(w, null);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
