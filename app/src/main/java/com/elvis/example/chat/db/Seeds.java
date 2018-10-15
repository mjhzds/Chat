package com.elvis.example.chat.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class Seeds {
    @Property
    private String sid;
    @Property
    private String seed;
    @Generated(hash = 522067143)
    public Seeds(String sid, String seed) {
        this.sid = sid;
        this.seed = seed;
    }
    @Generated(hash = 1786890624)
    public Seeds() {
    }
    public String getSid() {
        return this.sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getSeed() {
        return this.seed;
    }
    public void setSeed(String seed) {
        this.seed = seed;
    }
    
}
