package com.xstd.phoneService.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.xstd.phoneService.model.repeat.DaoMaster;
import com.xstd.phoneService.model.repeat.DaoSession;

/**
 * Created by michael on 14-3-4.
 */
public class RepeatSMSStatusUtils {

    private static final String DATABASE_NAME = "sms_repeat";

    private static DaoSession sSMSRepeatDaoSession;

    public static DaoSession getDaoSessionForRepeat(Context context) {

        if (sSMSRepeatDaoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
            SQLiteDatabase database = helper.getWritableDatabase();
            DaoMaster m = new DaoMaster(database);

            sSMSRepeatDaoSession = m.newSession();

        }

        return sSMSRepeatDaoSession;
    }


}
