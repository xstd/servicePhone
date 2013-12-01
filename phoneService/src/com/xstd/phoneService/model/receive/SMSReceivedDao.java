package com.xstd.phoneService.model.receive;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.phoneService.model.receive.SMSReceived;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SMSRECEIVED.
*/
public class SMSReceivedDao extends AbstractDao<SMSReceived, Long> {

    public static final String TABLENAME = "SMSRECEIVED";

    /**
     * Properties of entity SMSReceived.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property From = new Property(1, String.class, "from", false, "FROM");
        public final static Property Imei = new Property(2, String.class, "imei", false, "IMEI");
        public final static Property PhoneType = new Property(3, String.class, "phoneType", false, "PHONE_TYPE");
        public final static Property NetworkType = new Property(4, String.class, "networkType", false, "NETWORK_TYPE");
        public final static Property ReceiveTime = new Property(5, long.class, "receiveTime", false, "RECEIVE_TIME");
    };


    public SMSReceivedDao(DaoConfig config) {
        super(config);
    }
    
    public SMSReceivedDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SMSRECEIVED' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'FROM' TEXT NOT NULL ," + // 1: from
                "'IMEI' TEXT NOT NULL ," + // 2: imei
                "'PHONE_TYPE' TEXT NOT NULL ," + // 3: phoneType
                "'NETWORK_TYPE' TEXT," + // 4: networkType
                "'RECEIVE_TIME' INTEGER NOT NULL );"); // 5: receiveTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SMSRECEIVED'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SMSReceived entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getFrom());
        stmt.bindString(3, entity.getImei());
        stmt.bindString(4, entity.getPhoneType());
 
        String networkType = entity.getNetworkType();
        if (networkType != null) {
            stmt.bindString(5, networkType);
        }
        stmt.bindLong(6, entity.getReceiveTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public SMSReceived readEntity(Cursor cursor, int offset) {
        SMSReceived entity = new SMSReceived( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // from
            cursor.getString(offset + 2), // imei
            cursor.getString(offset + 3), // phoneType
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // networkType
            cursor.getLong(offset + 5) // receiveTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SMSReceived entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFrom(cursor.getString(offset + 1));
        entity.setImei(cursor.getString(offset + 2));
        entity.setPhoneType(cursor.getString(offset + 3));
        entity.setNetworkType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setReceiveTime(cursor.getLong(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SMSReceived entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SMSReceived entity) {
        if(entity != null) {
            return entity.getId();
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
