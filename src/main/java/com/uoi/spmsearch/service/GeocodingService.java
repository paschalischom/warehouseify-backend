package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.AddressRequest;
import com.uoi.spmsearch.dto.LocationRequest;
import com.uoi.spmsearch.dto.PointOfInterest;
import com.uoi.spmsearch.dto.googlemaps.GoogleMapsForwardGeocodingResponseResult;
import com.uoi.spmsearch.dto.googlemaps.GoogleMapsReverseGeocodingResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GeocodingService {
    private final GoogleMapsService googleMapsService;
    private final PointOfInterestService pointOfInterestService;

    public PointOfInterest geocodeAddressToPointOfInterest(AddressRequest addressRequest)
            throws InterruptedException, ExecutionException, IOException {
        GoogleMapsForwardGeocodingResponseResult response = googleMapsService.forwardGeocode(addressRequest);
        return pointOfInterestService.createPoIFromResponse(response);
    }

    public PointOfInterest geocodeLocationToPointOfInterest(LocationRequest locationRequest)
            throws InterruptedException, ExecutionException, IOException {
        GoogleMapsReverseGeocodingResponseResult response = googleMapsService.reverseGeocode(locationRequest);
        return pointOfInterestService.createPoIFromResponse(response);
    }
}
