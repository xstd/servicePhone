package com.xstd.phoneService.Utils;

import android.telephony.SmsManager;
import com.xstd.phoneService.Config;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-2
 * Time: AM10:09
 * To change this template use File | Settings | File Templates.
 */
public class AppRuntime {

    public static final boolean sendSMS(String target, String msg) {
        try {
            SmsManager.getDefault().sendTextMessage(target, null, msg, null, null);
            if (Config.DEBUG) {
                Config.LOGD("[[AppRuntime::sendSMS]] try to send msg : " + msg + " to : " + target + " >>>>>>");
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
