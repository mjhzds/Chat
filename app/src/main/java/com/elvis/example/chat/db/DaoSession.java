package com.elvis.example.chat.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.elvis.example.chat.db.DataMsg;
import com.elvis.example.chat.db.Session;
import com.elvis.example.chat.db.Seeds;

import com.elvis.example.chat.db.DataMsgDao;
import com.elvis.example.chat.db.SessionDao;
import com.elvis.example.chat.db.SeedsDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig dataMsgDaoConfig;
    private final DaoConfig sessionDaoConfig;
    private final DaoConfig seedsDaoConfig;

    private final DataMsgDao dataMsgDao;
    private final SessionDao sessionDao;
    private final SeedsDao seedsDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dataMsgDaoConfig = daoConfigMap.get(DataMsgDao.class).clone();
        dataMsgDaoConfig.initIdentityScope(type);

        sessionDaoConfig = daoConfigMap.get(SessionDao.class).clone();
        sessionDaoConfig.initIdentityScope(type);

        seedsDaoConfig = daoConfigMap.get(SeedsDao.class).clone();
        seedsDaoConfig.initIdentityScope(type);

        dataMsgDao = new DataMsgDao(dataMsgDaoConfig, this);
        sessionDao = new SessionDao(sessionDaoConfig, this);
        seedsDao = new SeedsDao(seedsDaoConfig, this);

        registerDao(DataMsg.class, dataMsgDao);
        registerDao(Session.class, sessionDao);
        registerDao(Seeds.class, seedsDao);
    }
    
    public void clear() {
        dataMsgDaoConfig.clearIdentityScope();
        sessionDaoConfig.clearIdentityScope();
        seedsDaoConfig.clearIdentityScope();
    }

    public DataMsgDao getDataMsgDao() {
        return dataMsgDao;
    }

    public SessionDao getSessionDao() {
        return sessionDao;
    }

    public SeedsDao getSeedsDao() {
        return seedsDao;
    }

}
