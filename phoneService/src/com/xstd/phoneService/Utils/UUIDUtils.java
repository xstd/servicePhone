package com.xstd.phoneService.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by michael on 14-1-26.
 */
public class UUIDUtils {

    private static final String PREFS_FILE = "device_id.xml";
    private static final String PREFS_DEVICE_ID = "device_id";
    public static UUID uuid;

    public synchronized static void saveUUID(Context context, String uuid) {
        if (!TextUtils.isEmpty(uuid)) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
            prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
        }
    }

    public static UUID deviceUuidFactory(Context context) {
        if (uuid == null) {
            synchronized (UUIDUtils.class) {
                if (uuid == null) {
                    SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        uuid = UUID.fromString(id);
                    } else {
                        //应该和SIM卡绑定，如果不能和SIM卡绑定的话，就和设备绑定
                        String androidId = null;
                        if (isSIMCardReady(context)) {
                            androidId = UtilsRuntime.getIMSI(context);
                        }
                        if (TextUtils.isEmpty(androidId)) {
                            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        }
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        if (uuid != null) {
                            prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                        }
                    }
                }
            }
        }

        return uuid;
    }

    public static boolean isSIMCardReady(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (tm.getSimState()) {
            case TelephonyManager.SIM_STATE_READY:
                return true;
        }

        return false;
    }

}
