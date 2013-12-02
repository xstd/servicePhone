package com.xstd.phoneService.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.xstd.phoneService.model.receive.DaoMaster;
import com.xstd.phoneService.model.receive.DaoSession;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-2
 * Time: AM7:08
 * To change this template use File | Settings | File Templates.
 */
public class ReceivedDaoUtils {

    private static final String DATABASE_NAME = "receivd_db";

    private static DaoSession sDaoSession;

    public static DaoSession getDaoSession(Context context) {

        if (sDaoSession == null) {

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
            SQLiteDatabase database = helper.getWritableDatabase();
            DaoMaster m = new DaoMaster(database);

            sDaoSession = m.newSession();

        }

        return sDaoSession;
    }

}
