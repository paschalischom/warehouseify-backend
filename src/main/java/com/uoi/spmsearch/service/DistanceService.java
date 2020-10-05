package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.*;
import com.uoi.spmsearch.dto.distancematrix.DistanceMatrixResponseResult;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DistanceService {

    private final GoogleMapsService googleMapsService;

    private double getClosestDimension(double poiDim, double mbrLowerDim, double mbrUpperDim) {
        if (poiDim < mbrLowerDim) {
            return mbrLowerDim;
        } else return Math.min(poiDim, mbrUpperDim);
    }

    private GeoPoint getClosestMBRPoint(PointOfInterest origin, RTreeBase destination) {
        double destinationLat;
        double destinationLng;
        if (destination instanceof RTreeNode) {
            destinationLat = getClosestDimension(origin.getLat(),
                    ((RTreeNode) destination).getMbr().getSouthwest().getLatitude(),
                    ((RTreeNode) destination).getMbr().getNortheast().getLatitude());
            destinationLng = getClosestDimension(origin.getLng(),
                    ((RTreeNode) destination).getMbr().getSouthwest().getLongitude(),
                    ((RTreeNode) destination).getMbr().getNortheast().getLongitude());
        } else if (destination instanceof RTreeLeaf) {
            destinationLat = ((RTreeLeaf) destination).getGeopoint().getLatitude();
            destinationLng = ((RTreeLeaf) destination).getGeopoint().getLongitude();
        } else {
            throw new UnsupportedOperationException();
        }
        return new GeoPoint(destinationLat, destinationLng);
    }

    public Map<String, Integer> calculateGoogleDistance(PointOfInterest origin, List<RTreeBase> destinations) throws InterruptedException, ExecutionException, IOException {
        List<GeoPoint> destinationsCoords = new ArrayList<>();
        for (RTreeBase destination: destinations) {
            destinationsCoords.add(getClosestMBRPoint(origin, destination));
        }
        Map<String, Integer> distances = new HashMap<>();
        int i;
        for (i = 0; i < destinations.size(); i += 25) {
            DistanceMatrixResponseResult result = googleMapsService.
                    calculateDistance(origin, destinationsCoords.subList(i, Math.min(destinationsCoords.size(), i+ 25)));
            System.out.println(i);
            for (int j = i; j < Math.min(destinationsCoords.size(), i+ 25); j++) {
                distances.put(destinations.get(j).getId(), result.getRows().get(0).getElements().get(j%25).getDistance().getValue());
            }
        }

        return distances;
    }

    public Map<String, Integer> calculateHaversineDistance(PointOfInterest origin, List<RTreeBase> destinations) {
        final int R = 6371; // Earth's radius

        double latOrigin = origin.getLat();
        double lngOrigin = origin.getLng();

        Map<String, Integer> distances = new HashMap<>();
        for (RTreeBase destination: destinations) {
            GeoPoint closestMBRPoint = getClosestMBRPoint(origin, destination);

            double latDistance = Math.toRadians(closestMBRPoint.getLatitude() - latOrigin);
            double lonDistance = Math.toRadians(closestMBRPoint.getLongitude() - lngOrigin);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(latOrigin)) * Math.cos(Math.toRadians(closestMBRPoint.getLatitude()))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c * 1000; // convert to meters

            distance = Math.pow(distance, 2);

            distances.put(destination.getId(), (int) Math.sqrt(distance));
        }

        return distances;
    }
}
