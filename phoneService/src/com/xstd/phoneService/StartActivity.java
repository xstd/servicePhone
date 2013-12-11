package com.xstd.phoneService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.plugin.common.utils.UtilsRuntime;
import com.umeng.analytics.MobclickAgent;
import com.xstd.phoneService.Utils.StatusDaoUtils;
import com.xstd.phoneService.firstService.DemoService;
import com.xstd.phoneService.model.status.SMSStatus;
import com.xstd.phoneService.model.status.SMSStatusDao;

import java.util.List;

public class StartActivity extends Activity {

    private EditText mFilterET;
    private EditText mCountET;

    private View mConfirmBtn;

    private TextView mStatusTV;

    private Switch mSwitcher;

    private SMSStatusDao mStatusDao;
    private SMSStatus mStatus;

    private static final int UPDATE_STATUS = 10000;
    private Handler  mHandler = new Handler() {
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
                mHandler.sendEmptyMessage(UPDATE_STATUS);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mFilterET = (EditText) findViewById(R.id.filter);
        mCountET = (EditText) findViewById(R.id.smsCount);
        mConfirmBtn = findViewById(R.id.confirm);
        mStatusTV = (TextView) findViewById(R.id.status);
        mSwitcher = (Switch) findViewById(R.id.abort_switch);

        mSwitcher.setChecked(SettingManager.getInstance().getFilterOpen());
        mSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingManager.getInstance().setFilterOpen(isChecked);
            }
        });

        String filters = SettingManager.getInstance().getFilter();
        if (TextUtils.isEmpty(filters)) {
            SettingManager.getInstance().setFilter(Config.FILTER);
        }

        mFilterET.setText(SettingManager.getInstance().getFilter());
        mCountET.setText(String.valueOf(SettingManager.getInstance().getSMSCount()));

        mStatusDao = StatusDaoUtils.getDaoSession(getApplicationContext()).getSMSStatusDao();


        mConfirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SettingManager.getInstance().setFilter(mFilterET.getText().toString());
                SettingManager.getInstance().setSMSCount(Integer.valueOf(mCountET.getText().toString()));

                updateStatus();

                Toast.makeText(StartActivity.this, "设置成功", Toast.LENGTH_LONG).show();
            }
        });

        registerReceiver(mStatusBRC, new IntentFilter(DemoService.UPDATE_STATUS));
        updateStatus();
    }

    @Override
    public void onStart() {
        super.onStart();
        MobclickAgent.onResume(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leave_list:
                Intent i = new Intent();
                i.setClass(getApplicationContext(), LeftListActivity.class);
                startActivity(i);
                break;
            case R.id.sent_list:
                Intent i1 = new Intent();
                i1.setClass(getApplicationContext(), SentListActivity.class);
                startActivity(i1);
                break;
        }

        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        MobclickAgent.onPause(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mStatusBRC);
    }

    private void updateStatus() {
        Config.LOGD("[[updateStatus]] >>>>>><<<<<<<<");

        List<SMSStatus> list = mStatusDao.queryBuilder().where(SMSStatusDao.Properties.ServerID.eq(100100)).build().forCurrentThread().list();
        if (list != null && list.size() > 0) {
            mStatus = list.get(0);
        } else {
            mStatus = new SMSStatus();
            mStatus.setServerID(100100);
        }
        mStatusTV.setText("[[短信手机状态]]:"
                              + "\n最后接收时间 : " + UtilsRuntime.debugFormatTime(mStatus.getLastReceivedTime() != null ? mStatus.getLastReceivedTime() : 0)
                              + "\n已经接收到短信数 : " + (mStatus.getReceviedCount() != null ? mStatus.getReceviedCount() : 0)
                              + "\n最后发送时间 : " + UtilsRuntime.debugFormatTime(mStatus.getLastSentTime() != null ? mStatus.getLastSentTime() : 0)
                              + "\n已经发送的短信数 : " + (mStatus.getSentCount() != null ? mStatus.getSentCount() : 0)
                              + "\n剩余短信数 : " + (mStatus.getLeaveCount() != null ? mStatus.getLeaveCount() : 0)
                              + "\n\n[[接受到的短信类型]]:"
                              + "\n移动 : " + (mStatus.getCmnetCount() != null ? mStatus.getCmnetCount() : 0)
                              + "\n联通 : " + (mStatus.getUnicomCount() != null ? mStatus.getUnicomCount() : 0)
                              + "\n电信 : " + (mStatus.getTelecomCount() != null ? mStatus.getTelecomCount() : 0)
                              + "\n铁通 : " + (mStatus.getSubwayCount() != null ? mStatus.getSubwayCount() : 0)
                              + "\n未知 : " + (mStatus.getUnknownCount() != null ? mStatus.getUnknownCount() : 0));
    }

}
