package com.uoi.spmsearch.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.uoi.spmsearch.dto.LocationRequest;
import com.uoi.spmsearch.dto.PointOfInterest;
import com.uoi.spmsearch.dto.googlemaps.*;
import com.uoi.spmsearch.errorhandling.ExternalServiceUnavailableException;
import com.uoi.spmsearch.errorhandling.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PointOfInterestService {

    private final Firestore db;
    private final UserService userService;
    private final GoogleMapsService googleMapsService;

    private String getStateFromAddressComponents(List<AddressComponent> addressComponents) {
        for (AddressComponent addressComponent: addressComponents) {
            if (addressComponent.getTypes().contains("administrative_area_level_1")) {
                return addressComponent.getLongName();
            }
        }
        return "N/A";
    }

    private DocumentSnapshot getPoISnapshot(String userUID, String poiUID) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("users").document(userUID).collection("pointsOfInterest").document(poiUID);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        return future.get();
    }

    private boolean poiExists(String userUID, String poiUID) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getPoISnapshot(userUID, poiUID);

        if (!document.exists()) {
            throw new ResourceNotFoundException(PointOfInterest.class, poiUID);
        }
        return document.exists();
    }

    public PointOfInterest createPoIFromResponse(GoogleMapsResponseResult response) {
        System.out.println(response.getStatus());
        PointOfInterest retPoI = new PointOfInterest();

        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);

        switch (response.getStatus()) {
            case "OK":
                // Get the first result which is the most relevant of the returned results
                // Suggested by Google, check:
                // https://developers.google.com/maps/documentation/geocoding/web-service-best-practices#ParsingJSON
                Result topResult = response.getResults().get(0);

                retPoI.setLat(topResult.getGeometry().getLocation().getLatitude());
                retPoI.setLng(topResult.getGeometry().getLocation().getLongitude());
                retPoI.setAddress(topResult.getFormattedAddress());

                List<AddressComponent> addressComponents = topResult.getAddressComponents();
                String state = getStateFromAddressComponents(addressComponents);

                retPoI.setState(state);
                retPoI.setUpdated(date.toString());
                retPoI.setError(false);

                retPoI.setLocationType(topResult.getGeometry().getLocationType());
                retPoI.setResponseStatus("OK");

                retPoI.setViewport(topResult.getGeometry().getViewport());
                break;
            case "ZERO_RESULTS":
                if (response instanceof GoogleMapsReverseGeocodingResponseResult) {
                    retPoI.setAddress("N/A");
                }

                retPoI.setState("N/A");
                retPoI.setUpdated(date.toString());
                retPoI.setError(false);

                retPoI.setResponseStatus("ZERO_RESULTS");
                break;
            default:
                String ex;
                if (response.getErrorMessage() != null) {
                    ex = response.getErrorMessage();
                } else {
                    ex = "Error message not provided.";
                }
                throw new ExternalServiceUnavailableException(ex);
        }

        return retPoI;
    }

    public PointOfInterest createPoIForFirestore(String userUID, LocationRequest locationRequest)
            throws ExecutionException, InterruptedException, IOException, ResourceNotFoundException {
        if (userService.userExists(userUID)) {
            GoogleMapsReverseGeocodingResponseResult response = googleMapsService.reverseGeocode(locationRequest);
            PointOfInterest pointOfInterest = createPoIFromResponse(response);

            // This is due to Google returning a latLng pair which depends on the geocoding process
            // It's not always the exact point the user inputted
            pointOfInterest.setLat(locationRequest.getLatitude());
            pointOfInterest.setLng(locationRequest.getLongitude());

            return pointOfInterest;
        } else {
            return null;
        }
    }

    public Map<String, PointOfInterest> addPoIToFirestore(String userUID, PointOfInterest pointOfInterest)
            throws ExecutionException, InterruptedException, ResourceNotFoundException {
        if (userService.userExists(userUID)) {
            ApiFuture<DocumentReference> addedDocRef = db.collection("users").document(userUID)
                    .collection("pointsOfInterest").add(pointOfInterest);
            String firestoreGeneratedUID = addedDocRef.get().getId();

            return Map.of(firestoreGeneratedUID, pointOfInterest);
        } else {
            return Map.of();
        }
    }

    public PointOfInterest getPoIFromFirestore(String userUID, String poiUID) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getPoISnapshot(userUID, poiUID);

        if (document.exists()) {
            return document.toObject(PointOfInterest.class);
        } else {
            throw new ResourceNotFoundException(PointOfInterest.class, poiUID);
        }
    }

    public void deletePoIFromFirestore(String userUID, String poiUID)
            throws ExecutionException, InterruptedException, ResourceNotFoundException {
        if (userService.userExists(userUID) && poiExists(userUID, poiUID)) {
            db.collection("users").document(userUID).collection("pointsOfInterest").document(poiUID).delete();
        }
    }

    public void deletePoiBatchFromFirestore(String userUID, String[] poiUIDs)
            throws ExecutionException, InterruptedException, ResourceNotFoundException {
        if (userService.userExists(userUID)) {
            for (String poiUID : poiUIDs) {
                if (poiExists(userUID, poiUID)) {
                    db.collection("users").document(userUID).collection("pointsOfInterest").document(poiUID).delete();
                }
            }
        }
    }

    public void editPoIFromFirestore(String userUID, String poiUID, PointOfInterest newPoI)
            throws ExecutionException, InterruptedException, ResourceNotFoundException {
        if (poiExists(userUID, poiUID)) {
            DocumentSnapshot documentSnapshot = getPoISnapshot(userUID, poiUID);

            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);
            newPoI.setUpdated(date.toString());

            documentSnapshot.getReference().set(newPoI, SetOptions.merge());
        }
    }
}
