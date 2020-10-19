package com.uoi.spmsearch.controller;
import com.uoi.spmsearch.dto.AddressRequest;
import com.uoi.spmsearch.dto.LocationRequest;
import com.uoi.spmsearch.dto.PointOfInterest;
import com.uoi.spmsearch.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/geocoding")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class GeocodingController {

    private final GeocodingService geocodingService;

    @PostMapping(value = "/forward")
    public PointOfInterest getForwardGeocode(@RequestBody AddressRequest addressRequest)
            throws IOException, ExecutionException, InterruptedException {
        return geocodingService.geocodeAddressToPointOfInterest(addressRequest);
    }

    @PostMapping(value = "/reverse")
    public PointOfInterest getReverseGeocode(@RequestBody LocationRequest locationRequest)
            throws IOException, ExecutionException, InterruptedException {
        return geocodingService.geocodeLocationToPointOfInterest(locationRequest);
    }
}
