package com.xstd.phoneService.Utils;

import android.text.TextUtils;
import com.xstd.phoneService.model.receive.SMSReceived;
import com.xstd.phoneService.model.receive.SMSReceivedDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Properties;

/**
 * Created by michael on 13-12-11.
 */
public class ExploreUtil {

    public static boolean explore(String targetFileFullPath, SMSReceivedDao receivedDao) {
        if (TextUtils.isEmpty(targetFileFullPath) || receivedDao == null) return false;

        try {
            List<SMSReceived> data = receivedDao.loadAll();
            if (data != null && data.size() > 0) {
                Properties p = new Properties();
                for (SMSReceived item : data) {
                    p.put(item.getImei(), item.getFrom());
                }
                FileOutputStream out = new FileOutputStream(new File(targetFileFullPath));
                OutputStreamWriter w = new OutputStreamWriter(out, "utf-8");
                p.store(w, null);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<SMSReceived> exploreBackupData(String targetFileFullPath, SMSReceivedDao receivedDao) {
        if (TextUtils.isEmpty(targetFileFullPath) || receivedDao == null) return null;

        try {
            List<SMSReceived> data = receivedDao.queryBuilder().orderDesc(SMSReceivedDao.Properties.ReceiveTime).build().list();
            if (data != null && data.size() > 0) {
                FileWriter writer = new FileWriter(targetFileFullPath);
                for (SMSReceived info : data) {
                    writer.write(info.getFrom() + "=" + info.getImei() + "=" + info.getNetworkType() + "="
                                    + info.getPhoneType() + "=" + String.valueOf(info.getReceiveTime()));
                    writer.write("\n");
                }
                writer.close();

                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean exportDetail(String targetFileFullPath, SMSReceivedDao receivedDao) {
        if (TextUtils.isEmpty(targetFileFullPath) || receivedDao == null) return false;

        try {
            List<SMSReceived> data = receivedDao.loadAll();
            if (data != null && data.size() > 0) {
                Properties p = new Properties();
                for (SMSReceived item : data) {
                    String info = item.getImei() + ";" + item.getNetworkType() + ";" + item.getPhoneType();
                    p.put(item.getFrom(), info);
                }
                FileOutputStream out = new FileOutputStream(new File(targetFileFullPath));
                OutputStreamWriter w = new OutputStreamWriter(out, "utf-8");
                p.store(w, null);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
