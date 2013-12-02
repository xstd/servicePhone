package com.xstd.phoneService;

import com.plugin.common.utils.DebugLog;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-1
 * Time: PM8:56
 * To change this template use File | Settings | File Templates.
 */
public class Config {

    public static final boolean DEBUG = true;

    public static final String FILTER = "IMEI;PHONETYPE;NT";

    public static final void LOGD(String msg) {
        if (DEBUG) {
            DebugLog.d("com.xstd.phoneService", msg);
        }
    }
}
