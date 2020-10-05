package com.uoi.spmsearch.dto.queryranges;

public class BuildingSizes {
    String idHigh;
    String idLow;
    float buildingSizeHigh;
    float buildingSizeLow;

    public BuildingSizes(String idHigh, String idLow, float buildingSizeHigh, float buildingSizeLow) {
        this.idHigh = idHigh;
        this.idLow = idLow;
        this.buildingSizeHigh = buildingSizeHigh;
        this.buildingSizeLow = buildingSizeLow;
    }

    public BuildingSizes() {
    }

    public String getIdHigh() {
        return idHigh;
    }

    public void setIdHigh(String idHigh) {
        this.idHigh = idHigh;
    }

    public String getIdLow() {
        return idLow;
    }

    public void setIdLow(String idLow) {
        this.idLow = idLow;
    }

    public float getBuildingSizeHigh() {
        return buildingSizeHigh;
    }

    public void setBuildingSizeHigh(float buildingSizeHigh) {
        this.buildingSizeHigh = buildingSizeHigh;
    }

    public float getBuildingSizeLow() {
        return buildingSizeLow;
    }

    public void setBuildingSizeLow(float buildingSizeLow) {
        this.buildingSizeLow = buildingSizeLow;
    }
}
