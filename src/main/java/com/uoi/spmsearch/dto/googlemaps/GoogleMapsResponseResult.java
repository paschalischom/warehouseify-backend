package com.uoi.spmsearch.dto.googlemaps;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uoi.spmsearch.dto.googlemaps.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleMapsResponseResult {

    String status;
    List<Result> results;
    @Builder.Default
    @JsonProperty("error_message")
    String errorMessage = null;
}
