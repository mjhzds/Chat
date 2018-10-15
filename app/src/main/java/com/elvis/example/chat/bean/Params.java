package com.elvis.example.chat.bean;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

public class Params {
    private String pairing_desc;
    private Pairing e;
    private Element g;
    private Element h;

    public String getPairing_desc() {
        return pairing_desc;
    }

    public void setPairing_desc(String pairing_desc) {
        this.pairing_desc = pairing_desc;
    }

    public Pairing getE() {
        return e;
    }

    public void setE(Pairing e) {
        this.e = e;
    }

    public Element getG() {
        return g;
    }

    public void setG(Element g) {
        this.g = g;
    }

    public Element getH() {
        return h;
    }

    public void setH(Element h) {
        this.h = h;
    }

    public Element getHat_alpha() {
        return hat_alpha;
    }

    public void setHat_alpha(Element hat_alpha) {
        this.hat_alpha = hat_alpha;
    }

    private Element hat_alpha;
}
