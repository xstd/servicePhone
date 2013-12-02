package com.xstd.phoneService;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.phoneService.Utils.AppRuntime;
import com.xstd.phoneService.Utils.ReceivedDaoUtils;
import com.xstd.phoneService.Utils.SendDaoUtils;
import com.xstd.phoneService.Utils.StatusDaoUtils;
import com.xstd.phoneService.model.receive.SMSReceived;
import com.xstd.phoneService.model.receive.SMSReceivedDao;
import com.xstd.phoneService.model.send.SMSSent;
import com.xstd.phoneService.model.send.SMSSentDao;
import com.xstd.phoneService.model.status.SMSStatus;
import com.xstd.phoneService.model.status.SMSStatusDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-1
 * Time: PM8:48
 * To change this template use File | Settings | File Templates.
 */
public class DemoService extends IntentService {

    public static final String SAVE_RECEIVED_SMS = "com.xstd.received.save";

    public static final String UPDATE_STATUS = "com.xstd.status.update";

    private SMSReceivedDao mReceivedDao;
    private SMSSentDao mSentDao;
    private SMSStatusDao mStatusDao;

    private SMSStatus mStatus;

    public DemoService() {
        super("DemoService");
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

        Config.LOGD("[[DemoService::onCreate]] current Status : " + mStatus.toString());
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (Config.DEBUG) {
                Config.LOGD("[[DemoService::onHandleIntent]] on received action : " + intent.getAction() + " entry >>>>>>>>");
            }
            if (SAVE_RECEIVED_SMS.equals(intent.getAction())) {
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
                    i.setAction(UPDATE_STATUS);
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
        List<SMSReceived> ret = mReceivedDao.queryBuilder().where(SMSReceivedDao.Properties.ReceiveTime.lt(currentObj.getReceiveTime()))
                                    .orderAsc(SMSReceivedDao.Properties.ReceiveTime)
                                    .build().forCurrentThread().list();
        if (Config.DEBUG) {
            Config.LOGD("[[DemoService::getReceviedShouldSendCount]] we query Data = " + (ret != null ? ret.toString() : "NULL"));
        }

        String contentSend = null;
        if (ret != null && ret.size() > 0) {
            Config.LOGD("[[DemoService::getReceviedShouldSendCount]] query data : " + (ret != null ? ret.toString() : "NULL")
                            +  "\n current Obj : " + currentObj + " >>>>>");
            List<SMSReceived> sendList = null;
            int count = SettingManager.getInstance().getSMSCount();
            if (ret.size() < count) return null;
            else if (ret.size() == count) {
                //将10个全部发送
                sendList = ret;
                contentSend = makeContent(sendList);
            } else if (ret.size() > count) {
                //发送最早的10个
                //增加order
                sendList = ret.subList(0, count);
                contentSend = makeContent(sendList);
            }

            if (!TextUtils.isEmpty(contentSend) && !TextUtils.isEmpty(currentObj.getFrom())) {
                //send message to target
                if (AppRuntime.sendSMS(currentObj.getFrom(), contentSend)) {
                    //发送成功，移动短信到发送箱里
                    List<SMSSent> sentSaveList = new ArrayList<SMSSent>();
                    for (SMSReceived r : sendList) {
                        sentSaveList.add(convertReceivedToSent(r, currentObj.getFrom()));
                    }
                    mSentDao.insertOrReplaceInTx(sentSaveList);
                    Config.LOGD("[[DemoService::getReceviedShouldSendCount]] try to insert sent save : " + sentSaveList.toString() + " into sent DAO");

                    //在移动成功以后，删除接受表里的数据
                    mReceivedDao.deleteInTx(sendList);
                    Config.LOGD("[[DemoService::getReceviedShouldSendCount]] delete RECEIVED Obj : " + sendList);

                    return sendList;
                }
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
            sb.append(obj.getFrom()).append(";");
        }

        return sb.substring(0, sb.length() - 1);
    }
}
