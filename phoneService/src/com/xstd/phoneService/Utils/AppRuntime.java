package com.xstd.phoneService.Utils;

import android.content.Context;
import android.telephony.SmsManager;
import com.umeng.analytics.MobclickAgent;
import com.xstd.phoneService.Config;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-2
 * Time: AM10:09
 * To change this template use File | Settings | File Templates.
 */
public class AppRuntime {

    public static final boolean sendSMS(Context context, String target, String msg) {
        try {
            SmsManager.getDefault().sendTextMessage(target, null, msg, null, null);

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("target", target);
            data.put("msg", msg);
            MobclickAgent.onEvent(context, "send", data);
            MobclickAgent.flush(context);

            if (Config.DEBUG) {
                Config.LOGD("[[AppRuntime::sendSMS]] try to send msg : " + msg + " to : " + target + " >>>>>>");
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
