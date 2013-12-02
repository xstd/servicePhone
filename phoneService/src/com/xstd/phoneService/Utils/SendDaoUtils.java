package com.xstd.phoneService.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.xstd.phoneService.model.send.DaoMaster;
import com.xstd.phoneService.model.send.DaoSession;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-2
 * Time: AM7:11
 * To change this template use File | Settings | File Templates.
 */
public class SendDaoUtils {

    private static final String DATABASE_NAME = "send_db";

    private static DaoSession sDaoSessionSent;

    public static DaoSession getDaoSession(Context context) {

        if (sDaoSessionSent == null) {

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
            SQLiteDatabase database = helper.getWritableDatabase();
            DaoMaster m = new DaoMaster(database);

            sDaoSessionSent = m.newSession();

        }

        return sDaoSessionSent;
    }

}
