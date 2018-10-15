package com.elvis.example.chat.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DATA_MSG".
*/
public class DataMsgDao extends AbstractDao<DataMsg, String> {

    public static final String TABLENAME = "DATA_MSG";

    /**
     * Properties of entity DataMsg.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Size = new Property(0, int.class, "size", false, "SIZE");
        public final static Property From = new Property(1, String.class, "from", false, "FROM");
        public final static Property Sid = new Property(2, String.class, "sid", true, "SID");
        public final static Property Pid = new Property(3, String.class, "pid", false, "PID");
        public final static Property Index = new Property(4, int.class, "index", false, "INDEX");
        public final static Property Hint_first = new Property(5, byte[].class, "hint_first", false, "HINT_FIRST");
        public final static Property U = new Property(6, byte[].class, "u", false, "U");
        public final static Property H = new Property(7, byte[].class, "h", false, "H");
        public final static Property V = new Property(8, byte[].class, "v", false, "V");
        public final static Property U_field = new Property(9, int.class, "u_field", false, "U_FIELD");
        public final static Property H_field = new Property(10, int.class, "h_field", false, "H_FIELD");
        public final static Property V_field = new Property(11, int.class, "v_field", false, "V_FIELD");
        public final static Property Hint_field = new Property(12, int.class, "hint_field", false, "HINT_FIELD");
        public final static Property Hash = new Property(13, byte[].class, "hash", false, "HASH");
    }


    public DataMsgDao(DaoConfig config) {
        super(config);
    }
    
    public DataMsgDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DATA_MSG\" (" + //
                "\"SIZE\" INTEGER NOT NULL ," + // 0: size
                "\"FROM\" TEXT," + // 1: from
                "\"SID\" TEXT PRIMARY KEY NOT NULL ," + // 2: sid
                "\"PID\" TEXT," + // 3: pid
                "\"INDEX\" INTEGER NOT NULL ," + // 4: index
                "\"HINT_FIRST\" BLOB," + // 5: hint_first
                "\"U\" BLOB," + // 6: u
                "\"H\" BLOB," + // 7: h
                "\"V\" BLOB," + // 8: v
                "\"U_FIELD\" INTEGER NOT NULL ," + // 9: u_field
                "\"H_FIELD\" INTEGER NOT NULL ," + // 10: h_field
                "\"V_FIELD\" INTEGER NOT NULL ," + // 11: v_field
                "\"HINT_FIELD\" INTEGER NOT NULL ," + // 12: hint_field
                "\"HASH\" BLOB);"); // 13: hash
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DATA_MSG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DataMsg entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getSize());
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(2, from);
        }
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(3, sid);
        }
 
        String pid = entity.getPid();
        if (pid != null) {
            stmt.bindString(4, pid);
        }
        stmt.bindLong(5, entity.getIndex());
 
        byte[] hint_first = entity.getHint_first();
        if (hint_first != null) {
            stmt.bindBlob(6, hint_first);
        }
 
        byte[] u = entity.getU();
        if (u != null) {
            stmt.bindBlob(7, u);
        }
 
        byte[] h = entity.getH();
        if (h != null) {
            stmt.bindBlob(8, h);
        }
 
        byte[] v = entity.getV();
        if (v != null) {
            stmt.bindBlob(9, v);
        }
        stmt.bindLong(10, entity.getU_field());
        stmt.bindLong(11, entity.getH_field());
        stmt.bindLong(12, entity.getV_field());
        stmt.bindLong(13, entity.getHint_field());
 
        byte[] hash = entity.getHash();
        if (hash != null) {
            stmt.bindBlob(14, hash);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DataMsg entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getSize());
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(2, from);
        }
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(3, sid);
        }
 
        String pid = entity.getPid();
        if (pid != null) {
            stmt.bindString(4, pid);
        }
        stmt.bindLong(5, entity.getIndex());
 
        byte[] hint_first = entity.getHint_first();
        if (hint_first != null) {
            stmt.bindBlob(6, hint_first);
        }
 
        byte[] u = entity.getU();
        if (u != null) {
            stmt.bindBlob(7, u);
        }
 
        byte[] h = entity.getH();
        if (h != null) {
            stmt.bindBlob(8, h);
        }
 
        byte[] v = entity.getV();
        if (v != null) {
            stmt.bindBlob(9, v);
        }
        stmt.bindLong(10, entity.getU_field());
        stmt.bindLong(11, entity.getH_field());
        stmt.bindLong(12, entity.getV_field());
        stmt.bindLong(13, entity.getHint_field());
 
        byte[] hash = entity.getHash();
        if (hash != null) {
            stmt.bindBlob(14, hash);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2);
    }    

    @Override
    public DataMsg readEntity(Cursor cursor, int offset) {
        DataMsg entity = new DataMsg( //
            cursor.getInt(offset + 0), // size
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // from
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // sid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // pid
            cursor.getInt(offset + 4), // index
            cursor.isNull(offset + 5) ? null : cursor.getBlob(offset + 5), // hint_first
            cursor.isNull(offset + 6) ? null : cursor.getBlob(offset + 6), // u
            cursor.isNull(offset + 7) ? null : cursor.getBlob(offset + 7), // h
            cursor.isNull(offset + 8) ? null : cursor.getBlob(offset + 8), // v
            cursor.getInt(offset + 9), // u_field
            cursor.getInt(offset + 10), // h_field
            cursor.getInt(offset + 11), // v_field
            cursor.getInt(offset + 12), // hint_field
            cursor.isNull(offset + 13) ? null : cursor.getBlob(offset + 13) // hash
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DataMsg entity, int offset) {
        entity.setSize(cursor.getInt(offset + 0));
        entity.setFrom(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPid(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setIndex(cursor.getInt(offset + 4));
        entity.setHint_first(cursor.isNull(offset + 5) ? null : cursor.getBlob(offset + 5));
        entity.setU(cursor.isNull(offset + 6) ? null : cursor.getBlob(offset + 6));
        entity.setH(cursor.isNull(offset + 7) ? null : cursor.getBlob(offset + 7));
        entity.setV(cursor.isNull(offset + 8) ? null : cursor.getBlob(offset + 8));
        entity.setU_field(cursor.getInt(offset + 9));
        entity.setH_field(cursor.getInt(offset + 10));
        entity.setV_field(cursor.getInt(offset + 11));
        entity.setHint_field(cursor.getInt(offset + 12));
        entity.setHash(cursor.isNull(offset + 13) ? null : cursor.getBlob(offset + 13));
     }
    
    @Override
    protected final String updateKeyAfterInsert(DataMsg entity, long rowId) {
        return entity.getSid();
    }
    
    @Override
    public String getKey(DataMsg entity) {
        if(entity != null) {
            return entity.getSid();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DataMsg entity) {
        return entity.getSid() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
