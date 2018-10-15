package com.elvis.example.chat.bean;


import java.io.Serializable;

import it.unisa.dia.gas.jpbc.Element;

public class Message implements Serializable {
    private String pid;
    private String sid;
    private int index;
    private Element hint_first;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Element getHint_first() {
        return hint_first;
    }

    public void setHint_first(Element hint_first) {
        this.hint_first = hint_first;
    }
}
