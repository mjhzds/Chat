package com.elvis.example.chat.db;

import com.elvis.example.chat.utils.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class Session {
    @Id
    private String sid;
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> pids;
    @Property
    private byte[] msk;
    @Generated(hash = 238638800)
    public Session(String sid, List<String> pids, byte[] msk) {
        this.sid = sid;
        this.pids = pids;
        this.msk = msk;
    }
    @Generated(hash = 1317889643)
    public Session() {
    }
    public String getSid() {
        return this.sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public List<String> getPids() {
        return this.pids;
    }
    public void setPids(List<String> pids) {
        this.pids = pids;
    }
    public byte[] getMsk() {
        return this.msk;
    }
    public void setMsk(byte[] msk) {
        this.msk = msk;
    }
}
