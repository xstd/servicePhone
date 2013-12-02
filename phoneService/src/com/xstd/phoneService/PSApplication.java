package com.xstd.phoneService;

import android.app.Application;
import com.plugin.common.utils.UtilsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-1
 * Time: PM8:07
 * To change this template use File | Settings | File Templates.
 */
public class PSApplication extends Application {

    @Override
    public void onCreate() {
        SettingManager.getInstance().init(getApplicationContext());
        UtilsConfig.init(getApplicationContext());

        initUMeng();
    }

    private void initUMeng() {
        MobclickAgent.setSessionContinueMillis(60 * 1000);
        MobclickAgent.setDebugMode(false);
        com.umeng.common.Log.LOG = false;
        MobclickAgent.onError(this);

        MobclickAgent.flush(this);
    }

}
