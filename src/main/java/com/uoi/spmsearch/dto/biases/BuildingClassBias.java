package com.uoi.spmsearch.dto.biases;

import java.util.Map;


public class BuildingClassBias extends BaseBias {
    Map<String, String> buildingClassBiasMap;

    public BuildingClassBias(String all, Map<String, String> buildingClassBiasMap) {
        super(all);
        this.buildingClassBiasMap = buildingClassBiasMap;
    }

    public BuildingClassBias() {
    }

    public Map<String, String> getBuildingClassBiasMap() {
        return buildingClassBiasMap;
    }

    public void setBuildingClassBiasMap(Map<String, String> buildingClassBiasMap) {
        this.buildingClassBiasMap = buildingClassBiasMap;
    }
}
