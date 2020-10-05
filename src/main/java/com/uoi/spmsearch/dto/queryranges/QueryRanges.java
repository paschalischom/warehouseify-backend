package com.uoi.spmsearch.dto.queryranges;

import java.util.List;

public class QueryRanges {

    List<String> buildingClasses;
    BuildingSizes buildingSizes;
    List<String> subTypes;
    Statuses statuses;

    public QueryRanges(List<String> buildingClasses, BuildingSizes buildingSizes, List<String> subTypes, Statuses statuses) {
        this.buildingClasses = buildingClasses;
        this.buildingSizes = buildingSizes;
        this.subTypes = subTypes;
        this.statuses = statuses;
    }

    public QueryRanges() {
    }

    public List<String> getBuildingClasses() {
        return buildingClasses;
    }

    public void setBuildingClasses(List<String> buildingClasses) {
        this.buildingClasses = buildingClasses;
    }

    public BuildingSizes getBuildingSizes() {
        return buildingSizes;
    }

    public void setBuildingSizes(BuildingSizes buildingSizes) {
        this.buildingSizes = buildingSizes;
    }

    public List<String> getSubTypes() {
        return subTypes;
    }

    public void setSubTypes(List<String> subTypes) {
        this.subTypes = subTypes;
    }

    public Statuses getStatuses() {
        return statuses;
    }

    public void setStatuses(Statuses statuses) {
        this.statuses = statuses;
    }
}
