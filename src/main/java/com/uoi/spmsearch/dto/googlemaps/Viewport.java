package com.uoi.spmsearch.dto.googlemaps;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Viewport {

    Location northeast;
    Location southwest;
}
