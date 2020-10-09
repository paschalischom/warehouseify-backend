package com.uoi.spmsearch.dto.biases;

import java.util.Map;

public class DistanceBias extends BaseBias {
    Map<String, String> distanceBiasMap;

    public DistanceBias(String all, Map<String, String> distanceBiasMap) {
        super(all);
        this.distanceBiasMap = distanceBiasMap;
    }

    public DistanceBias() {
    }

    public Map<String, String> getDistanceBiasMap() {
        return distanceBiasMap;
    }

    public void setDistanceBiasMap(Map<String, String> distanceBiasMap) {
        this.distanceBiasMap = distanceBiasMap;
    }
}
