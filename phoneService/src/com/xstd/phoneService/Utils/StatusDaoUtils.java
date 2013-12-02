package com.xstd.phoneService.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.xstd.phoneService.model.status.DaoMaster;
import com.xstd.phoneService.model.status.DaoSession;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-12-2
 * Time: PM2:08
 * To change this template use File | Settings | File Templates.
 */
public class StatusDaoUtils {

    private static final String DATABASE_NAME = "statu_db";

    private static DaoSession sStatusDaoSession;

    public static DaoSession getDaoSession(Context context) {

        if (sStatusDaoSession == null) {

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
            SQLiteDatabase database = helper.getWritableDatabase();
            DaoMaster m = new DaoMaster(database);

            sStatusDaoSession = m.newSession();

        }

        return sStatusDaoSession;
    }

}
