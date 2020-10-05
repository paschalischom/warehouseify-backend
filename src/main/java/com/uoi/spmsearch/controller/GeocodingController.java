package com.uoi.spmsearch.controller;
import com.uoi.spmsearch.dto.AddressRequest;
import com.uoi.spmsearch.dto.LocationRequest;
import com.uoi.spmsearch.dto.PointOfInterest;
import com.uoi.spmsearch.dto.googlemaps.GoogleMapsForwardGeocodingResponseResult;
import com.uoi.spmsearch.dto.googlemaps.GoogleMapsReverseGeocodingResponseResult;
import com.uoi.spmsearch.service.GoogleMapsService;
import com.uoi.spmsearch.service.PointOfInterestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/*
 * Although POST methods, GeocodingController serves as a middle-man between front-end
 * and the google API (essentially a GET) . Methods modifying their response
 * based on the request body must not be GET methods (HTTP/1.1 - Roy Fielding).
 * The request body must have no semantic meaning to the GET method.
 */
@RestController
@RequestMapping("/api/geocoding")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class GeocodingController {

    private final GoogleMapsService googleMapsService;
    private final PointOfInterestService pointOfInterestService;

    @PostMapping(value = "/forward")
    public PointOfInterest getForwardGeocode(@RequestBody AddressRequest addressRequest) throws IOException, ExecutionException, InterruptedException {
        GoogleMapsForwardGeocodingResponseResult response = googleMapsService.forwardGeocode(addressRequest);
        return pointOfInterestService.createPoIFromResponse(response);
    }

    @PostMapping(value = "/reverse")
    public PointOfInterest getReverseGeocode(@RequestBody LocationRequest locationRequest) throws IOException, ExecutionException, InterruptedException {
        GoogleMapsReverseGeocodingResponseResult response = googleMapsService.reverseGeocode(locationRequest);
        return pointOfInterestService.createPoIFromResponse(response);
    }
}
