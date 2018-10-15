package com.elvis.example.chat.bean;

import it.unisa.dia.gas.jpbc.Element;

public class KeyPair {
    private Element pub;

    public Element getPub() {
        return pub;
    }

    public void setPub(Element pub) {
        this.pub = pub;
    }

    public Element getPrv() {
        return prv;
    }

    public void setPrv(Element prv) {
        this.prv = prv;
    }

    private Element prv;
}
