package com.elvis.example.chat.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.elvis.example.chat.utils.StringConverter;
import java.util.List;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SESSION".
*/
public class SessionDao extends AbstractDao<Session, String> {

    public static final String TABLENAME = "SESSION";

    /**
     * Properties of entity Session.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Sid = new Property(0, String.class, "sid", true, "SID");
        public final static Property Pids = new Property(1, String.class, "pids", false, "PIDS");
        public final static Property Msk = new Property(2, byte[].class, "msk", false, "MSK");
    }

    private final StringConverter pidsConverter = new StringConverter();

    public SessionDao(DaoConfig config) {
        super(config);
    }
    
    public SessionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SESSION\" (" + //
                "\"SID\" TEXT PRIMARY KEY NOT NULL ," + // 0: sid
                "\"PIDS\" TEXT," + // 1: pids
                "\"MSK\" BLOB);"); // 2: msk
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SESSION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Session entity) {
        stmt.clearBindings();
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(1, sid);
        }
 
        List pids = entity.getPids();
        if (pids != null) {
            stmt.bindString(2, pidsConverter.convertToDatabaseValue(pids));
        }
 
        byte[] msk = entity.getMsk();
        if (msk != null) {
            stmt.bindBlob(3, msk);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Session entity) {
        stmt.clearBindings();
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(1, sid);
        }
 
        List pids = entity.getPids();
        if (pids != null) {
            stmt.bindString(2, pidsConverter.convertToDatabaseValue(pids));
        }
 
        byte[] msk = entity.getMsk();
        if (msk != null) {
            stmt.bindBlob(3, msk);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public Session readEntity(Cursor cursor, int offset) {
        Session entity = new Session( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // sid
            cursor.isNull(offset + 1) ? null : pidsConverter.convertToEntityProperty(cursor.getString(offset + 1)), // pids
            cursor.isNull(offset + 2) ? null : cursor.getBlob(offset + 2) // msk
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Session entity, int offset) {
        entity.setSid(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setPids(cursor.isNull(offset + 1) ? null : pidsConverter.convertToEntityProperty(cursor.getString(offset + 1)));
        entity.setMsk(cursor.isNull(offset + 2) ? null : cursor.getBlob(offset + 2));
     }
    
    @Override
    protected final String updateKeyAfterInsert(Session entity, long rowId) {
        return entity.getSid();
    }
    
    @Override
    public String getKey(Session entity) {
        if(entity != null) {
            return entity.getSid();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Session entity) {
        return entity.getSid() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}