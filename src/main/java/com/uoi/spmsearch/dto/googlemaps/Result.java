package com.uoi.spmsearch.dto.googlemaps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @JsonProperty("address_components")
    List<AddressComponent> addressComponents;
    @JsonProperty("formatted_address")
    String formattedAddress;
    Geometry geometry;
    @JsonProperty("place_id")
    String placeId;
    @JsonProperty("plus_code")
    PlusCode plusCode; // This field is returned only when forward geocoding, null otherwise
    List<String> types;
}
