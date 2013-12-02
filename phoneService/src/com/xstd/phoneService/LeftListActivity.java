package com.xstd.phoneService;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.common.utils.UtilsRuntime;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                SMSReceivedDao receivedDao = ReceivedDaoUtils.getDaoSession(getApplicationContext()).getSMSReceivedDao();
                if (receivedDao != null) {
                    final List<SMSReceived> data = receivedDao.loadAll();
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