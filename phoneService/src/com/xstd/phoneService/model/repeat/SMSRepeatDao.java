package com.xstd.phoneService.model.repeat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.phoneService.model.repeat.SMSRepeat;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SMSREPEAT.
*/
public class SMSRepeatDao extends AbstractDao<SMSRepeat, String> {

    public static final String TABLENAME = "SMSREPEAT";

    /**
     * Properties of entity SMSRepeat.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property From = new Property(0, String.class, "from", true, "FROM");
        public final static Property PhoneType = new Property(1, String.class, "phoneType", false, "PHONE_TYPE");
        public final static Property Imsi = new Property(2, String.class, "imsi", false, "IMSI");
        public final static Property NetworkType = new Property(3, String.class, "networkType", false, "NETWORK_TYPE");
        public final static Property ReceiveTime = new Property(4, long.class, "receiveTime", false, "RECEIVE_TIME");
        public final static Property RepeatCount = new Property(5, Long.class, "repeatCount", false, "REPEAT_COUNT");
    };


    public SMSRepeatDao(DaoConfig config) {
        super(config);
    }
    
    public SMSRepeatDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SMSREPEAT' (" + //
                "'FROM' TEXT PRIMARY KEY NOT NULL ," + // 0: from
                "'PHONE_TYPE' TEXT NOT NULL ," + // 1: phoneType
                "'IMSI' TEXT NOT NULL ," + // 2: imsi
                "'NETWORK_TYPE' TEXT NOT NULL ," + // 3: networkType
                "'RECEIVE_TIME' INTEGER NOT NULL ," + // 4: receiveTime
                "'REPEAT_COUNT' INTEGER);"); // 5: repeatCount
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SMSREPEAT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SMSRepeat entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getFrom());
        stmt.bindString(2, entity.getPhoneType());
        stmt.bindString(3, entity.getImsi());
        stmt.bindString(4, entity.getNetworkType());
        stmt.bindLong(5, entity.getReceiveTime());
 
        Long repeatCount = entity.getRepeatCount();
        if (repeatCount != null) {
            stmt.bindLong(6, repeatCount);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SMSRepeat readEntity(Cursor cursor, int offset) {
        SMSRepeat entity = new SMSRepeat( //
            cursor.getString(offset + 0), // from
            cursor.getString(offset + 1), // phoneType
            cursor.getString(offset + 2), // imsi
            cursor.getString(offset + 3), // networkType
            cursor.getLong(offset + 4), // receiveTime
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5) // repeatCount
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SMSRepeat entity, int offset) {
        entity.setFrom(cursor.getString(offset + 0));
        entity.setPhoneType(cursor.getString(offset + 1));
        entity.setImsi(cursor.getString(offset + 2));
        entity.setNetworkType(cursor.getString(offset + 3));
        entity.setReceiveTime(cursor.getLong(offset + 4));
        entity.setRepeatCount(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(SMSRepeat entity, long rowId) {
        return entity.getFrom();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(SMSRepeat entity) {
        if(entity != null) {
            return entity.getFrom();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
