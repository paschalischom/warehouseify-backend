package com.uoi.spmsearch.dto;

import com.uoi.spmsearch.dto.biases.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryRanking {

    DistanceBias distanceBias;
    PriceBias priceBias;
    BuildingClassBias buildingClassBias;
    BuildingSizeBias buildingSizeBias;
    LotSizeBias lotSizeBias;
}
