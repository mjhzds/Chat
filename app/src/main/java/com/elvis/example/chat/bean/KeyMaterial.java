package com.elvis.example.chat.bean;

import it.unisa.dia.gas.jpbc.Element;

public class KeyMaterial {
    private Element left_key;
    private Element right_key;
    private Element hint_plus;

    public Element getLeft_key() {
        return left_key;
    }

    public void setLeft_key(Element left_key) {
        this.left_key = left_key;
    }

    public Element getRight_key() {
        return right_key;
    }

    public void setRight_key(Element right_key) {
        this.right_key = right_key;
    }

    public Element getHint_plus() {
        return hint_plus;
    }

    public void setHint_plus(Element hint_plus) {
        this.hint_plus = hint_plus;
    }
}
