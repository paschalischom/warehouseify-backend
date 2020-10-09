package com.uoi.spmsearch.dto.biases;

public class LotSizeBias extends BaseBias {
    String na;
    String size;

    public LotSizeBias(String all, String na, String size) {
        super(all);
        this.na = na;
        this.size = size;
    }

    public LotSizeBias() {
    }

    public String getNa() {
        return na;
    }

    public void setNa(String na) {
        this.na = na;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
