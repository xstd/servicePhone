package com.xstd.phoneService;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.common.utils.UtilsRuntime;
import com.umeng.analytics.MobclickAgent;
import com.xstd.phoneService.Utils.ExploreUtil;
import com.xstd.phoneService.Utils.ReceivedDaoUtils;
import com.xstd.phoneService.Utils.StatusDaoUtils;
import com.xstd.phoneService.firstService.DemoService;
import com.xstd.phoneService.model.receive.SMSReceived;
import com.xstd.phoneService.model.receive.SMSReceivedDao;
import com.xstd.phoneService.model.status.SMSStatus;
import com.xstd.phoneService.model.status.SMSStatusDao;
import com.xstd.phoneService.old_code.SentListActivity;
import com.xstd.phoneService.secondeService.SMSFilterService;
import com.xstd.phoneService.setting.SettingManager;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by michael on 13-12-10.
 */
public class MainServiceInfoActivity extends Activity {

    private EditText mFilterET;

    private TextView mStatusTV;

    private Switch mSwitcher;

    private Switch mAutoSwitcher;

    private SMSStatusDao mStatusDao;
    private SMSStatus mStatus;

    private SMSReceivedDao mSMSReceivedDao;

    private static final String DEBUG_DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";

    public static String formatTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEBUG_DATE_FORMAT);
        return dateFormat.format(time);
    }

    private static final int UPDATE_STATUS = 10000;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_STATUS:
                    updateStatus();
                    break;
            }
        }
    };

    private BroadcastReceiver mStatusBRC = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DemoService.UPDATE_STATUS)) {
                mHandler.removeMessages(UPDATE_STATUS);
                mHandler.sendEmptyMessageDelayed(UPDATE_STATUS, 1000);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.second);

        mSMSReceivedDao = ReceivedDaoUtils.getDaoSession(getApplicationContext()).getSMSReceivedDao();
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.app_name) + "(V" + UtilsRuntime.getVersionName(getApplicationContext()) + ")");

        mFilterET = (EditText) findViewById(R.id.filter);
        mStatusTV = (TextView) findViewById(R.id.status);
        mSwitcher = (Switch) findViewById(R.id.abort_switch);

        mAutoSwitcher = (Switch) findViewById(R.id.auto_sync);

        mAutoSwitcher.setChecked(SettingManager.getInstance().getAutoSync());
        mAutoSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingManager.getInstance().setAutoSync(isChecked);
            }
        });

        mSwitcher.setChecked(SettingManager.getInstance().getFilterOpen());
        mSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingManager.getInstance().setFilterOpen(isChecked);
            }
        });

        Switch soundSwitch = (Switch) findViewById(R.id.sound_open);
        soundSwitch.setChecked(SettingManager.getInstance().getSoundOpen());
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingManager.getInstance().setSoundOpen(isChecked);
            }
        });

        String filters = SettingManager.getInstance().getFilter();
        if (TextUtils.isEmpty(filters)) {
            SettingManager.getInstance().setFilter(Config.FILTER);
        }

        mFilterET.setText(SettingManager.getInstance().getFilter());

        mStatusDao = StatusDaoUtils.getDaoSession(getApplicationContext()).getSMSStatusDao();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(DemoService.UPDATE_STATUS);
        lbm.registerReceiver(mStatusBRC, filter);
        updateStatus();

        //check Google Service if runging for SMS
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(getApplicationContext(), SMSFilterService.class);
        startService(serviceIntent);

    }

    @Override
    public void onStart() {
        super.onStart();
        MobclickAgent.onResume(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_seconde, menu);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_data:
                //同步数据到服务器
                ExploreUtil.syncUpdateIMSI2Phone(getApplicationContext(), mSMSReceivedDao, mHandler, SettingManager.getInstance().getTodaySMSCount());
                break;
            case R.id.sync_alldata:
                //同步全部数据到服务器
                ExploreUtil.syncUpdateIMSI2Phone(getApplicationContext(), mSMSReceivedDao, mHandler, -1);
                break;
            case R.id.leave_list:
                Intent i = new Intent();
                i.setClass(getApplicationContext(), ReceivedSMSListActivity.class);
                startActivity(i);
                break;
            case R.id.sent_list:
                Intent i1 = new Intent();
                i1.setClass(getApplicationContext(), SentListActivity.class);
                startActivity(i1);
                break;
            case R.id.repeat_list:
                Intent repeatI = new Intent();
                repeatI.setClass(getApplicationContext(), RepeatLogActivity.class);
                startActivity(repeatI);
                break;
            case R.id.explore:
                CustomThreadPool.asyncWork(new Runnable() {
                    @Override
                    public void run() {
                        if (ExploreUtil.explore("/sdcard/phone_number_map.txt", mSMSReceivedDao)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "成功导出数据到/sdcard/phone_number_map.txt", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "导出数据到/sdcard/phone_number_map.txt 失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.move_data:
                moveData();
                break;
        }

        return true;
    }

    private void moveData() {
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                String time = formatTime(System.currentTimeMillis());
                final String targetFile = "/sdcard/phone_number_backup_detail-" + time + ".txt";
                List<SMSReceived> data = ExploreUtil.exploreBackupData(targetFile, mSMSReceivedDao);
                boolean backup = false;
                if (data != null && data.size() > 3000) {
                    List<SMSReceived> subList = data.subList(3000, data.size());
                    if (subList != null && subList.size() > 0) {
                        mSMSReceivedDao.deleteInTx(subList);

                        long count = mSMSReceivedDao.count();
                        List<SMSStatus> list = mStatusDao.queryBuilder().where(SMSStatusDao.Properties.ServerID.eq(100100)).build().forCurrentThread().list();
                        if (list != null && list.size() > 0) {
                            mStatus = list.get(0);
                        } else {
                            mStatus = new SMSStatus();
                            mStatus.setServerID(100100);
                        }
                        mStatus.setReceviedCount(count);
                        mStatusDao.insertOrReplace(mStatus);
                        backup = true;
                    }
                }

                if (backup) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), ("成功迁移数据到" + targetFile), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "迁移数据到" + targetFile + "失败", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                mHandler.sendEmptyMessage(UPDATE_STATUS);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        MobclickAgent.onPause(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
        lbm.unregisterReceiver(mStatusBRC);
    }

    private void updateStatus() {
        Config.LOGD("[[updateStatus]] >>>>>><<<<<<<<");

        synchronized (Config.gDBLock) {
            List<SMSStatus> list = mStatusDao.queryBuilder().where(SMSStatusDao.Properties.ServerID.eq(100100)).build().forCurrentThread().list();
            if (list != null && list.size() > 0) {
                mStatus = list.get(0);
            } else {
                mStatus = new SMSStatus();
                mStatus.setServerID(100100);
            }
            mStatusTV.setText("[[短信手机状态]]:"
                                  + "\n最后接收时间 : " + UtilsRuntime.debugFormatTime(SettingManager.getInstance().getLastReceivedSMSTime())
                                  + "\n最后上传时间 : " + UtilsRuntime.debugFormatTime(SettingManager.getInstance().getLastUpdateSMSReceivedTime())
                                  + "\n静态拦截短信数 : " + (mStatus.getReceviedCount() != null ? mStatus.getReceviedCount() : 0)
                                  + "\n动态拦截短信数 : " + SettingManager.getInstance().getDynamicSMSFilterCount()
                                  + "\n今天拦截短信数 : " + SettingManager.getInstance().getTodaySMSCount()
//                              + "\n最后发送时间 : " + UtilsRuntime.debugFormatTime(mStatus.getLastSentTime() != null ? mStatus.getLastSentTime() : 0)
//                              + "\n已经发送的短信数 : " + (mStatus.getSentCount() != null ? mStatus.getSentCount() : 0)
//                              + "\n剩余短信数 : " + (mStatus.getLeaveCount() != null ? mStatus.getLeaveCount() : 0)
                                  + "\n\n\n[[拦截到的短信类型]]:"
                                  + "\n移动 : " + (mStatus.getCmnetCount() != null ? mStatus.getCmnetCount() : 0)
                                  + "\n联通 : " + (mStatus.getUnicomCount() != null ? mStatus.getUnicomCount() : 0)
                                  + "\n电信 : " + (mStatus.getTelecomCount() != null ? mStatus.getTelecomCount() : 0)
                                  + "\n铁通 : " + (mStatus.getSubwayCount() != null ? mStatus.getSubwayCount() : 0)
                                  + "\n未知 : " + (mStatus.getUnknownCount() != null ? mStatus.getUnknownCount() : 0));
        }
    }
}