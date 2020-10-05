package com.uoi.spmsearch.model;

import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class State {

    @DocumentId
    String name;

    int numOfListings;
    GeoPoint geoCenter;
    List<GeoPoint> boundingPolygon;
    String abbreviation;
    Map<String, GeoPoint> viewport;

    public void setName(String name) {
        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
        this.name = capitalizedName.replace("-", " ");
    }

}
