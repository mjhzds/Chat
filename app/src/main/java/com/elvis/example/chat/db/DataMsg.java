package com.elvis.example.chat.db;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class DataMsg {
    @Property
    private int size;
    @Property
    private String from;
    @Id
    private String sid;
    @Property
    private String pid;
    @Property
    private int index;
    @Property
    private byte[] hint_first;
    @Property
    private byte[] u;
    @Property
    private byte[] h;
    @Property
    private byte[] v;
    @Property
    private int u_field;
    @Property
    private int h_field;
    @Property
    private int v_field;
    @Property
    private int hint_field;
    @Property
    private byte[] hash;
    @Generated(hash = 1638417787)
    public DataMsg(int size, String from, String sid, String pid, int index,
            byte[] hint_first, byte[] u, byte[] h, byte[] v, int u_field,
            int h_field, int v_field, int hint_field, byte[] hash) {
        this.size = size;
        this.from = from;
        this.sid = sid;
        this.pid = pid;
        this.index = index;
        this.hint_first = hint_first;
        this.u = u;
        this.h = h;
        this.v = v;
        this.u_field = u_field;
        this.h_field = h_field;
        this.v_field = v_field;
        this.hint_field = hint_field;
        this.hash = hash;
    }
    @Generated(hash = 2117090336)
    public DataMsg() {
    }
    public int getSize() {
        return this.size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String getFrom() {
        return this.from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getSid() {
        return this.sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    public String getPid() {
        return this.pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public byte[] getHint_first() {
        return this.hint_first;
    }
    public void setHint_first(byte[] hint_first) {
        this.hint_first = hint_first;
    }
    public byte[] getU() {
        return this.u;
    }
    public void setU(byte[] u) {
        this.u = u;
    }
    public byte[] getH() {
        return this.h;
    }
    public void setH(byte[] h) {
        this.h = h;
    }
    public byte[] getV() {
        return this.v;
    }
    public void setV(byte[] v) {
        this.v = v;
    }
    public int getU_field() {
        return this.u_field;
    }
    public void setU_field(int u_field) {
        this.u_field = u_field;
    }
    public int getH_field() {
        return this.h_field;
    }
    public void setH_field(int h_field) {
        this.h_field = h_field;
    }
    public int getV_field() {
        return this.v_field;
    }
    public void setV_field(int v_field) {
        this.v_field = v_field;
    }
    public int getHint_field() {
        return this.hint_field;
    }
    public void setHint_field(int hint_field) {
        this.hint_field = hint_field;
    }
    public byte[] getHash() {
        return this.hash;
    }
    public void setHash(byte[] hash) {
        this.hash = hash;
    }
}
