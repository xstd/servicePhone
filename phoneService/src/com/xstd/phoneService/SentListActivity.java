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
import com.xstd.phoneService.Utils.SendDaoUtils;
import com.xstd.phoneService.model.send.SMSSent;
import com.xstd.phoneService.model.send.SMSSentDao;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-3
 * Time: AM12:20
 * To change this template use File | Settings | File Templates.
 */
public class SentListActivity extends ListActivity {

    private Handler mHandler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                SMSSentDao sendDap = SendDaoUtils.getDaoSession(getApplicationContext()).getSMSSentDao();
                if (sendDap != null) {
                    final List<SMSSent> data = sendDap.loadAll();
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

        private List<SMSSent> dataList;

        public ListAdapter(List<SMSSent> data) {
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
            ((TextView) ret.findViewById(R.id.time)).setText("发送时间:" + UtilsRuntime.debugFormatTime(dataList.get(position).getSendTime()));
            ((TextView) ret.findViewById(R.id.more)).setText(dataList.get(position).getNetworkType()
                                                                 + " : " + dataList.get(position).getPhoneType()
                                                                 + "\n发送到的手机:" + dataList.get(position).getSendPhoneNumber() + "进行处理");

            return ret;
        }
    }
}