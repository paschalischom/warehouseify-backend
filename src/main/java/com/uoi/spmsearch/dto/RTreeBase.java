package com.uoi.spmsearch.dto;

import com.google.cloud.firestore.annotation.DocumentId;

public class RTreeBase implements Comparable<RTreeBase> {
    @DocumentId
    String id;
    int distance;

    public RTreeBase(String id, int distance) {
        this.id = id;
        this.distance = distance;
    }

    public RTreeBase() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(RTreeBase rTreeBase) {
        return Double.compare(this.getDistance(), rTreeBase.getDistance());
    }
}
