package com.uoi.spmsearch.dto.biases;

public class BuildingSizeBias extends BaseBias {
    String na;
    String size;

    public BuildingSizeBias(String all, String na, String size) {
        super(all);
        this.na = na;
        this.size = size;
    }

    public BuildingSizeBias() {
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
