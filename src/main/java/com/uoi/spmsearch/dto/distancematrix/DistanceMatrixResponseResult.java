package com.uoi.spmsearch.dto.distancematrix;

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
public class DistanceMatrixResponseResult {

    String status;
    @JsonProperty("origin_addresses")
    List<String> originAddresses;
    @JsonProperty("destination_addresses")
    List<String> destinationAddresses;
    List<Row> rows;
    @Builder.Default
    @JsonProperty("error_message")
    String errorMessage = null;
}
