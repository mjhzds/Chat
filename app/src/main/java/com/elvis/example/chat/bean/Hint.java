package com.elvis.example.chat.bean;

import it.unisa.dia.gas.jpbc.Element;

public class Hint {
    private Element beta; //X^i
    private Element hint_first;

    public Element getBeta() {
        return beta;
    }

    public void setBeta(Element beta) {
        this.beta = beta;
    }

    public Element getHint_first() {
        return hint_first;
    }

    public void setHint_first(Element hint_first) {
        this.hint_first = hint_first;
    }
}
