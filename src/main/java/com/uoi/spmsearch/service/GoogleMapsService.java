package com.uoi.spmsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uoi.spmsearch.dto.AddressRequest;
import com.uoi.spmsearch.dto.GeoPoint;
import com.uoi.spmsearch.dto.LocationRequest;
import com.uoi.spmsearch.dto.PointOfInterest;
import com.uoi.spmsearch.dto.distancematrix.DistanceMatrixResponseResult;
import com.uoi.spmsearch.dto.googlemaps.*;
import com.uoi.spmsearch.errorhandling.InvalidGeocodingArgumentsException;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GoogleMapsService {

    private final OkHttpClient httpClient;
    private final Request.Builder requestBuilder;

    private String executeRequest(String urlString) throws ExecutionException, InterruptedException, IOException {
        Request request  = requestBuilder
                .url(urlString)
                .build();
        CallbackFuture future = new CallbackFuture();
        httpClient.newCall(request).enqueue(future);
        Response response = future.get();
        assert response.body() != null;
        return response.body().string();
    }

    public GoogleMapsForwardGeocodingResponseResult forwardGeocode(AddressRequest addressRequest) throws InterruptedException, ExecutionException, IOException {
        if (addressRequest.getAddress() == null) {
            String params = "{ address: " + addressRequest.getAddress() + " }";
            throw new InvalidGeocodingArgumentsException(params);
        }
        String encodedAddress = URLEncoder.encode(addressRequest.getAddress(), StandardCharsets.UTF_8);
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "language=en&region=us&address=" + encodedAddress +
                "&key=<GEOCODING_API_KEY>";
        String response = executeRequest(urlString);
        return new ObjectMapper().readValue(response, GoogleMapsForwardGeocodingResponseResult.class);
    }

    public GoogleMapsReverseGeocodingResponseResult reverseGeocode(LocationRequest locationRequest) throws InterruptedException, ExecutionException, IOException {
        if (Double.isNaN(locationRequest.getLatitude()) || Double.isNaN(locationRequest.getLongitude())) {
            String params = "{ lat: " + locationRequest.getLatitude() + ", lng: " + locationRequest.getLongitude() + " }";
            throw new InvalidGeocodingArgumentsException(params);
        }
        String encodedLatLng = locationRequest.encodeReverseGeocodingUrl();
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "language=en&result_type=street_address&latlng=" + encodedLatLng +
                "&key=<GEOCODING_API_KEY>";
        String response = executeRequest(urlString);
        return new ObjectMapper().readValue(response, GoogleMapsReverseGeocodingResponseResult.class);
    }

    public DistanceMatrixResponseResult calculateDistance(PointOfInterest origin, List<GeoPoint> destinations) throws InterruptedException, ExecutionException, IOException {
        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&" +
                "origins=" + origin.getLat() + "," + origin.getLng() + "&destinations=");
        for (GeoPoint destination: destinations) {
            urlString.append(destination.getLatitude()).append(",").append(destination.getLongitude()).append("|");
        }
        urlString.setLength(urlString.length() - 1);
        urlString.append("&key=<DISTANCE_MATRIX_API_KEY>");
        //System.out.printf("Url: %s\n", urlString.toString());
        String response = executeRequest(urlString.toString());
        //System.out.println(response);
        return new ObjectMapper().readValue(response, DistanceMatrixResponseResult.class);
    }
}
