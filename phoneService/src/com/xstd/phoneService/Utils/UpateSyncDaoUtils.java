package com.xstd.phoneService.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by michael on 14-2-18.
 */
public class UpateSyncDaoUtils {

    private static final String DATABASE_NAME = "update_log_db";

    private static com.xstd.phoneService.model.update.DaoSession sSMSUpdateDaoSession;

    public static com.xstd.phoneService.model.update.DaoSession getDaoSessionForUpdate(Context context) {

        if (sSMSUpdateDaoSession == null) {
            com.xstd.phoneService.model.update.DaoMaster.DevOpenHelper helper = new com.xstd.phoneService.model.update.DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
            SQLiteDatabase database = helper.getWritableDatabase();
            com.xstd.phoneService.model.update.DaoMaster m = new com.xstd.phoneService.model.update.DaoMaster(database);

            sSMSUpdateDaoSession = m.newSession();

        }

        return sSMSUpdateDaoSession;
    }

}
