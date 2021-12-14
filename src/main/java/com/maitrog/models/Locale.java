package com.maitrog.models;

import java.io.Serializable;

public class Locale implements Serializable {
    private String ru;
    private String en;

    public Locale(String ru, String en) {
        this.ru = ru;
        this.en = en;
    }

    public Locale() {}

    public String getRu() {
        return ru;
    }

    public String getEn() {
        return en;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public void setEn(String en) {
        this.en = en;
    }
}
