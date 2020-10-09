package com.uoi.spmsearch.dto.biases;

import java.util.Map;

public class PriceBias extends BaseBias {
    Map<String, String> priceBiasMap;

    public PriceBias(String all, Map<String, String> priceBiasMap) {
        super(all);
        this.priceBiasMap = priceBiasMap;
    }

    public PriceBias() {
    }

    public Map<String, String> getPriceBiasMap() {
        return priceBiasMap;
    }

    public void setPriceBiasMap(Map<String, String> priceBiasMap) {
        this.priceBiasMap = priceBiasMap;
    }
}
