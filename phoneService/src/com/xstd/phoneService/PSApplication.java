package com.xstd.phoneService;

import android.app.Application;

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
    }

}
