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
import com.xstd.phoneService.Utils.RepeatSMSStatusUtils;
import com.xstd.phoneService.model.repeat.SMSRepeat;
import com.xstd.phoneService.model.repeat.SMSRepeatDao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by michael on 14-3-14.
 */
public class RepeatLogActivity extends ListActivity {

    private Handler mHandler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                SMSRepeatDao repeatDao = RepeatSMSStatusUtils.getDaoSessionForRepeat(getApplicationContext()).getSMSRepeatDao();
                if (repeatDao != null) {
                    final List<SMSRepeat> data = repeatDao.queryBuilder().orderDesc(SMSRepeatDao.Properties.RepeatCount).list();

                    Collections.sort(data, new Comparator<SMSRepeat>() {
                        @Override
                        public int compare(SMSRepeat lhs, SMSRepeat rhs) {
                            if (lhs.getRepeatCount() > rhs.getRepeatCount()) {
                                return -1;
                            } else if (lhs.getRepeatCount() < rhs.getRepeatCount()) {
                                return 1;
                            }

                            return 0;
                        }
                    });

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

        private List<SMSRepeat> dataList;

        public ListAdapter(List<SMSRepeat> data) {
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

            SMSRepeat item = dataList.get(position);
            ret.findViewById(R.id.number).setVisibility(View.VISIBLE);
            ret.findViewById(R.id.more).setVisibility(View.VISIBLE);

            ((TextView) ret.findViewById(R.id.index)).setText("[" + (dataList.size() - position) + "]");
            ((TextView) ret.findViewById(R.id.number)).setText("手机号码:" + item.getFrom());
            ((TextView) ret.findViewById(R.id.time)).setText("接收时间:" + UtilsRuntime.debugFormatTime(item.getReceiveTime()));
            ((TextView) ret.findViewById(R.id.more)).setText(item.getNetworkType() + " : " + item.getPhoneType() + " : 重复次数 " + item.getRepeatCount());
            ret.setBackgroundColor(0xff000000);

            return ret;
        }
    }

}