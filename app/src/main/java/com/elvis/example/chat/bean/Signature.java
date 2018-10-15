package com.elvis.example.chat.bean;

import it.unisa.dia.gas.jpbc.Element;

public class Signature {
    private Element u;
    private Element h;  //hashed msg
    private Element v;

    public Element getU() {
        return u;
    }

    public void setU(Element u) {
        this.u = u;
    }

    public Element getH() {
        return h;
    }

    public void setH(Element h) {
        this.h = h;
    }

    public Element getV() {
        return v;
    }

    public void setV(Element v) {
        this.v = v;
    }
}
