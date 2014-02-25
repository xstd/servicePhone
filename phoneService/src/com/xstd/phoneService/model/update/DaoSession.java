package com.xstd.phoneService.model.update;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.xstd.phoneService.model.update.SMSUpdateSyncStatus;

import com.xstd.phoneService.model.update.SMSUpdateSyncStatusDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig sMSUpdateSyncStatusDaoConfig;

    private final SMSUpdateSyncStatusDao sMSUpdateSyncStatusDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        sMSUpdateSyncStatusDaoConfig = daoConfigMap.get(SMSUpdateSyncStatusDao.class).clone();
        sMSUpdateSyncStatusDaoConfig.initIdentityScope(type);

        sMSUpdateSyncStatusDao = new SMSUpdateSyncStatusDao(sMSUpdateSyncStatusDaoConfig, this);

        registerDao(SMSUpdateSyncStatus.class, sMSUpdateSyncStatusDao);
    }
    
    public void clear() {
        sMSUpdateSyncStatusDaoConfig.getIdentityScope().clear();
    }

    public SMSUpdateSyncStatusDao getSMSUpdateSyncStatusDao() {
        return sMSUpdateSyncStatusDao;
    }

}
