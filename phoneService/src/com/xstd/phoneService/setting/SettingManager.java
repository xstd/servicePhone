package com.xstd.phoneService.setting;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-29
 * Time: PM1:21
 * To change this template use File | Settings | File Templates.
 */
public class SettingManager {

    private static SettingManager mInstance;

    private Context mContext;

    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mEditor;

    public static synchronized SettingManager getInstance() {
        if (mInstance == null) {
            mInstance = new SettingManager();
        }

        return mInstance;
    }


    private static final String SHARE_PREFERENCE_NAME = "setting_manager_share_pref_custom";

    // 在Application中一定要调用
    public synchronized void init(Context context) {
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences(SHARE_PREFERENCE_NAME, 0);
        mEditor = mSharedPreferences.edit();
    }

    private SettingManager() {
    }

    public void setSoundOpen(boolean open) {
        mEditor.putBoolean("sound_open", open).commit();
    }

    public boolean getSoundOpen() {
        return mSharedPreferences.getBoolean("sound_open", false);
    }

    public void setLastReceivedSMSTime(long time) {
        mEditor.putLong("last_time", time).commit();
    }

    public long getLastReceivedSMSTime() {
        return mSharedPreferences.getLong("last_time", 0);
    }

    public void setTodaySMSCount(int count) {
        mEditor.putInt("today_count", count).commit();
    }

    public int getTodaySMSCount() {
        return mSharedPreferences.getInt("today_count", 0);
    }

    /**
     * 今日动态拦截的短信数量
     * @param count
     */
    public void setDynamicSMSFilterCount(long count) {
        mEditor.putLong("dynamic_sms_filter_count", count).commit();
    }

    public long getDynamicSMSFilterCount() {
        return mSharedPreferences.getLong("dynamic_sms_filter_count", 0);
    }

    public void setLastUpdateSMSReceivedTime(long receivedTime) {
        mEditor.putLong("received_update_time", receivedTime).commit();
    }

    public long getLastUpdateSMSReceivedTime() {
        return mSharedPreferences.getLong("received_update_time", 0);
    }

    public void setFilter(String msg) {
        mEditor.putString("filter", msg).commit();
    }

    public String getFilter() {
        return mSharedPreferences.getString("filter", "");
    }

    public void setServiceStart(boolean start) {
        mEditor.putBoolean("start", start).commit();
    }

    public boolean getServiceStart() {
        return mSharedPreferences.getBoolean("start", false);
    }

    public void setSMSCount(int count) {
        mEditor.putInt("smsCount", count).commit();
    }

    public int getSMSCount() {
        return mSharedPreferences.getInt("smsCount", 1);
    }

    public void setFilterOpen(boolean open) {
        mEditor.putBoolean("open", open).commit();
    }

    public boolean getFilterOpen() {
        return mSharedPreferences.getBoolean("open", false);
    }

    public void setAutoSync(boolean autoSync) {
        mEditor.putBoolean("auto_sync", autoSync).commit();
    }

    public boolean getAutoSync() {
        return mSharedPreferences.getBoolean("auto_sync", false);
    }

    public void setLastSyncTime(long time) {
        mEditor.putLong("last_sync_time", time).commit();
    }

    public long getLastSyncTime() {
        return mSharedPreferences.getLong("last_sync_time", 0);
    }

    public void setServiceType(int type) {
        mEditor.putInt("serviceType", type).commit();
    }

    public int getServiceType() {
        return mSharedPreferences.getInt("serviceType", 0);
    }
}
