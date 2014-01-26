package com.xstd.phoneService;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.phoneService.Utils.ExploreUtil;
import com.xstd.phoneService.Utils.ReceivedDaoUtils;
import com.xstd.phoneService.model.receive.SMSReceived;
import com.xstd.phoneService.model.receive.SMSReceivedDao;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-2
 * Time: PM11:59
 * To change this template use File | Settings | File Templates.
 */
public class LeftListActivity extends ListActivity {

    private Handler mHandler = new Handler();

    private SMSReceivedDao mReceivedDao;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                mReceivedDao = ReceivedDaoUtils.getDaoSession(getApplicationContext()).getSMSReceivedDao();
                if (mReceivedDao != null) {
                    final List<SMSReceived> data = mReceivedDao.queryBuilder().orderDesc(SMSReceivedDao.Properties.ReceiveTime).list();
                    if (data != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setListAdapter(new ListAdapter(data));
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_left, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                CustomThreadPool.asyncWork(new Runnable() {
                    @Override
                    public void run() {
                        if (ExploreUtil.exportDetail("/sdcard/phone_number_map_detail.txt", mReceivedDao)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "成功导出数据到/sdcard/phone_number_map_detail.txt", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "导出数据到/sdcard/phone_number_map_detail.txt 失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                break;
        }

        return true;
    }

    private class ListAdapter extends BaseAdapter {

        private List<SMSReceived> dataList;

        public ListAdapter(List<SMSReceived> data) {
            dataList = data;
        }

        @Override
        public int getCount() {
            if (dataList != null) {
                return dataList.size();
            }

            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ret = convertView;
            if (ret == null) {
                ret = getLayoutInflater().inflate(R.layout.list_item, null);
            }

            ((TextView) ret.findViewById(R.id.number)).setText("手机号码:" + dataList.get(position).getFrom());
            ((TextView) ret.findViewById(R.id.time)).setText("接收时间:" + UtilsRuntime.debugFormatTime(dataList.get(position).getReceiveTime()));
            ((TextView) ret.findViewById(R.id.more)).setText(dataList.get(position).getNetworkType() + " : " + dataList.get(position).getPhoneType());

            return ret;
        }
    }

}