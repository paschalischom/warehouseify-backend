package com.uoi.spmsearch.dto;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Listing {

    @DocumentId
    String id;

    String title;
    String description;
    String state;

    String price;
    double priceHigh;
    double priceLow;
    String status;

    String propertyType;
    List<String> subTypes;
    String capRate;

    String buildingSize;
    String lotSize;
    String buildingClass;

    String spaces;
    String spaceAvailable;

    String yearBuilt;

    double longitude;
    double latitude;

    String url;

    Map<String, Integer> distancesToPoi;
    double listingScore;
}
