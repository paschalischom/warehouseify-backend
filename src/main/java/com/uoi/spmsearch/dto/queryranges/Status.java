package com.uoi.spmsearch.dto.queryranges;

public class Status {

    String idHigh;
    String idLow;
    float priceHigh;
    float priceLow;

    public Status(String idHigh, String idLow, float priceHigh, float priceLow) {
        this.idHigh = idHigh;
        this.idLow = idLow;
        this.priceHigh = priceHigh;
        this.priceLow = priceLow;
    }

    public Status() {
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

    public float getPriceHigh() {
        return priceHigh;
    }

    public void setPriceHigh(float priceHigh) {
        this.priceHigh = priceHigh;
    }

    public float getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(float priceLow) {
        this.priceLow = priceLow;
    }
}
