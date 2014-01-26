package com.xstd.phoneService.Utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.common.utils.GsonUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.plugin.internet.InternetUtils;
import com.xstd.phoneService.api.IMSIUpdateRequest;
import com.xstd.phoneService.api.IMSIUpdateResponse;
import com.xstd.phoneService.model.receive.SMSReceived;
import com.xstd.phoneService.model.receive.SMSReceivedDao;

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

    public static void syncUpdateIMSI2Phone(final Context context, final SMSReceivedDao receivedDao, final Handler handler) {
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    List<SMSReceived> data = receivedDao.loadAll();
                    HashMap<String, String> update = new HashMap<String, String>();
                    for (SMSReceived smsReceived : data) {
                        update.put(smsReceived.getImei(), smsReceived.getFrom());
                    }
                    upateIMSI2Service(context, update, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void upateIMSI2Service(final Context context, HashMap<String, String> data, final Handler handler) {
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
            String dataUpdate = GsonUtils.toJson(data);
            IMSIUpdateRequest request = new IMSIUpdateRequest(dataUpdate, unique);
            final IMSIUpdateResponse response = InternetUtils.request(context, request);
            if (response != null && response.result != -1) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int curtotal = response.result;
                        int upadteCount = response.uploadCont;
                        int orgCount = response.orgCount;
                        String show = "服务器现有:[" + curtotal + "]项，此次上传数据更新过了:[" + (curtotal - orgCount) + "]项";
                        Toast.makeText(context,
                                          show,
                                          Toast.LENGTH_LONG).show();
                    }
                });
            }

            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "上传失败，请重新上传", Toast.LENGTH_LONG).show();
            }
        });
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
