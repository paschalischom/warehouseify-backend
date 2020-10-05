package com.uoi.spmsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.uoi.spmsearch.dto.googlemaps.Viewport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.Double.NaN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointOfInterest {

    @Builder.Default
    double lat = NaN;
    @Builder.Default
    double lng = NaN;
    @Builder.Default
    String address = null;

    @Builder.Default
    double radius = 5000; // Meters

    String state;
    String updated;
    @Builder.Default
    String status = "Active";
    @Builder.Default
    Boolean error = true;

    String locationType;
    String responseStatus;

    Viewport viewport;
}
