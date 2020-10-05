package com.uoi.spmsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCustomization {

    String state;
    String buildingClass;
    String propertyType;
    String status;
    boolean buildingClassCheckbox;
    boolean propertyTypeCheckbox;
    @Builder.Default
    double priceRangeFrom = 0;
    @Builder.Default
    double priceRangeTo = Double.MAX_VALUE;


}
