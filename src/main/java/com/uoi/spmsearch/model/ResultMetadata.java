package com.uoi.spmsearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultMetadata {
    int maxDistance;
    double maxSalePrice;
    double maxLeasePrice;
    double maxBuildingSize;
    double maxLotSize;
}
