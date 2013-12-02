package com.xstd.phoneService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-1
 * Time: PM8:05
 * To change this template use File | Settings | File Templates.
 */
public class SMSFilterBRC extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            SettingManager.getInstance().init(context);
            SmsMessage[] messages = getMessagesFromIntent(intent);
            if (messages == null || messages.length == 0) {
                return;
            }

            for (SmsMessage message : messages) {
                String msg = message.getMessageBody();
                String address = message.getOriginatingAddress();

                if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(msg)
                        && !TextUtils.isEmpty(SettingManager.getInstance().getFilter())) {
                    String filters = SettingManager.getInstance().getFilter();
                    String[] keys = filters.split(";");
                    boolean shouldFilter = false;
                    for (String key : keys) {
                        if (msg.contains(key)) {
                            shouldFilter = true;
                            break;
                        }
                    }

                    if (Config.DEBUG) {
                        Config.LOGD("\n\n[[SMSFilterBRC::onReceive]] has receive SMS from : \n<<" + message.getDisplayOriginatingAddress()
                                        + ">>"
                                        + "\n || content : " + message.getMessageBody()
                                        + "\n || sms center = " + message.getServiceCenterAddress()
                                        + "\n || sms display origin address = " + message.getDisplayOriginatingAddress()
                                        + "\n || sms = " + msg
                                        + "\n || intent info = " + intent.toString()
                                        + "\n || filter keys = " + filters
                                        + "\n =================="
                                        + "\n\n");
                    }

                    if (shouldFilter) {
                        //表示需要过滤
                        if (TextUtils.isEmpty(address) || address.startsWith("10")) {
                            //当短信发送地址是以10开始或是地址是空的时候，表示这个短信是应该忽略的，因为可以是运营短信。
                            if (Config.DEBUG) {
                                Config.LOGD("\n[[PrivateSMSBRC::onReceive]] ignore this Message as the address is empty.\n");
                            }
                            return;
                        }

                        if (address.startsWith("+") == true && address.length() == 14) {
                            address = address.substring(3);
                        } else if (address.length() > 11) {
                            address = address.substring(address.length() - 11);
                        }

                        Intent i = new Intent();
                        i.setClass(context, DemoService.class);
                        i.putExtra("from", address);
                        i.putExtra("receiveTime", System.currentTimeMillis());

                        String[] datas = msg.split(" ");
                        if (datas == null) return;
                        for (String data : datas) {
                            if (data.startsWith("IMEI:")) {
                                i.putExtra("imei", data.substring("IMEI:".length()));
                            } else if (data.startsWith("PHONETYPE:")) {
                                i.putExtra("phoneType", data.substring("PHONETYPE:".length()));
                            } else if (data.startsWith("NT:")) {
                                int type = Integer.valueOf(data.substring("NT:".length()));
                                i.putExtra("nt", type);
                                switch (type) {
                                    case 1:
                                        i.putExtra("networkType", "移动");
                                        break;
                                    case 2:
                                        i.putExtra("networkType", "联通");
                                        break;
                                    case 3:
                                        i.putExtra("networkType", "电信");
                                        break;
                                    case 4:
                                        i.putExtra("networkType", "铁通");
                                        break;
                                    case -1:
                                        i.putExtra("networkType", "未知");
                                        break;
                                }
                            }
                        }

                        if (Config.DEBUG) {
                            Config.LOGD("[[SMSFilterBRC::onReceive]] start Service with info : " + i.getExtras().toString());
                        }

                        i.setAction(DemoService.SAVE_RECEIVED_SMS);
                        context.startService(i);

                        if (SettingManager.getInstance().getFilterOpen()) {
                            abortBroadcast();
                        }
                    }
                }
            }
        }
    }

    /**
     * 从Intent中获取短信的信息。
     *
     * @param intent
     * @return
     */
    private final SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }

}
