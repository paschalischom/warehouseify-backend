package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.*;
import com.uoi.spmsearch.dto.googlemaps.Result;
import com.uoi.spmsearch.model.ResultMetadata;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SearchService {

    private final UserService userService;
    private final RangeSearchService rangeSearchService;

    private ResultMetadata getResultMetadata(List<Listing> resultListings) {
        int maxDistance = 0;
        double maxSalePrice = 0;
        double maxLeasePrice = 0;
        double maxBuildingSize = 0;
        double maxLotSize = 0;

        for (Listing listing : resultListings) {
            for (Integer listingDistanceToPoi : listing.getDistancesToPoi().values()) {
                if (listingDistanceToPoi > maxDistance) {
                    maxDistance = listingDistanceToPoi;
                }
            }
            if (listing.getStatus().equals("For Sale")) {
                if (listing.getPriceLow() > maxSalePrice) {
                    maxSalePrice = listing.getPriceLow();
                }
            } else if (listing.getStatus().equals("For Lease")) {
                if (listing.getPriceLow() > maxLeasePrice) {
                    maxLeasePrice = listing.getPriceLow();
                }
            }
            if (listing.getBuildingSizeNum() > maxBuildingSize) {
                maxBuildingSize = listing.getBuildingSizeNum();
            }
            if (listing.getLotSizeNum() > maxLotSize) {
                maxLotSize = listing.getLotSizeNum();
            }
        }
        return new ResultMetadata(maxDistance, maxSalePrice, maxLeasePrice, maxBuildingSize, maxLotSize);
    }

    private boolean filterListing(Listing listing, QueryFilter queryFilter) {

        if (!queryFilter.getState().equals("") &&
                !listing.getState().equals(queryFilter.getState())) {
            return true;
        }
        if (!queryFilter.getBuildingClass().equals("") &&
                !listing.getBuildingClass().equals(queryFilter.getBuildingClass())) {
            if (!queryFilter.isBuildingClassCheckbox()) {
                return true;
            }
            else if (queryFilter.isBuildingClassCheckbox() && !listing.getBuildingClass().equals("N/A")) {
                return true;
            }
        }
        if (!queryFilter.getPropertyType().equals("") &&
                !listing.getSubTypes().contains(queryFilter.getPropertyType())) {
            if (!queryFilter.isPropertyTypeCheckbox()) {
                return true;
            }
            else if (queryFilter.isPropertyTypeCheckbox() && !listing.getSubTypes().contains("N/A")) {
                return true;
            }
        }
        if (!queryFilter.getStatus().equals("") && !listing.getStatus().equals(queryFilter.getStatus())) {
            return true;
        }
        if (listing.getPriceHigh() == -1) { // The listing doesn't have an upper pricing bound
            if (queryFilter.getPriceRangeFrom() > listing.getPriceLow() ||
                    queryFilter.getPriceRangeTo() < listing.getPriceLow()) {
                return true;
            }
        } else if (queryFilter.getPriceRangeFrom() > listing.getPriceLow() ||
                queryFilter.getPriceRangeTo() < listing.getPriceHigh()) {
            return true;
        }
        return false;
    }

    private double calculateScore(Listing listing, QueryRanking queryRanking, ResultMetadata resultMetadata) {
        double score = 0;
        // Distance
        if (queryRanking.getDistanceBias().getAll() != null) {
            double universalBias = Double.parseDouble(queryRanking.getDistanceBias().getAll());
            for (String poiUID : listing.getDistancesToPoi().keySet()) {
                score += universalBias * ((double)listing.getDistancesToPoi().get(poiUID) / (double)resultMetadata.getMaxDistance());
            }
        }
        else {
            for (String poiUID : listing.getDistancesToPoi().keySet()) {
                score += Double.parseDouble(queryRanking.getDistanceBias().getDistanceBiasMap().get(poiUID)) *
                        ((double)listing.getDistancesToPoi().get(poiUID) / (double)resultMetadata.getMaxDistance());
            }
        }
        // Price
        if (queryRanking.getPriceBias().getAll() != null) {
            double normalizedPrice = 0;
            if (listing.getStatus().equals("For Sale")) {
                normalizedPrice = listing.getPriceLow() / resultMetadata.getMaxSalePrice();
            } else if (listing.getStatus().equals("For Lease")) {
                normalizedPrice = listing.getPriceLow() / resultMetadata.getMaxLeasePrice();
            }
            if (listing.getPriceLow() != -1) {
                score += Double.parseDouble(queryRanking.getPriceBias().getAll()) * normalizedPrice;
            }
        }
        else {
            if (listing.getPriceLow() != -1) {
                if (listing.getStatus().equals("For Sale")) {
                    score += Double.parseDouble(queryRanking.getPriceBias().getPriceBiasMap().get("forSale")) *
                            (listing.getPriceLow() / resultMetadata.getMaxSalePrice());
                } else if (listing.getStatus().equals("For Lease")) {
                    score += Double.parseDouble(queryRanking.getPriceBias().getPriceBiasMap().get("forLease")) *
                            (listing.getPriceLow() / resultMetadata.getMaxLeasePrice());
                }
            }
        }
        // Building Class
        if (queryRanking.getBuildingClassBias().getAll() != null) {
            score += Double.parseDouble(queryRanking.getBuildingClassBias().getAll()) * score;
        }
        else {
            String listingClass = listing.getBuildingClass();
            if (listing.getBuildingClass().equals("N/A")) {
                listingClass = "NA";
            }
            score += Double.parseDouble(queryRanking.getBuildingClassBias().
                    getBuildingClassBiasMap().get(listingClass)) * score;
        }
        // Building Size
        if (queryRanking.getBuildingSizeBias().getAll() != null) {
            if (listing.getBuildingSizeNum() != -1) {
                score += Double.parseDouble(queryRanking.getBuildingSizeBias().getAll()) *
                        (listing.getBuildingSizeNum() / resultMetadata.getMaxBuildingSize());
            } else {
                score += Double.parseDouble(queryRanking.getBuildingSizeBias().getAll()) * score;
            }
        } else {
            if (listing.getBuildingSizeNum() != -1) {
                score += Double.parseDouble(queryRanking.getBuildingSizeBias().getSize()) *
                        (listing.getBuildingSizeNum() / resultMetadata.getMaxBuildingSize());
            } else {
                score += Double.parseDouble(queryRanking.getBuildingSizeBias().getNa()) * score;
            }
        }
        // Lot Size
        if (queryRanking.getLotSizeBias().getAll() != null) {
            if (listing.getLotSizeNum() != -1) {
                score += Double.parseDouble(queryRanking.getLotSizeBias().getAll()) *
                        (listing.getLotSizeNum() / resultMetadata.getMaxLotSize());
            } else {
                score += Double.parseDouble(queryRanking.getLotSizeBias().getAll()) * score;
            }
        } else {
            if (listing.getLotSizeNum() != -1) {
                score += Double.parseDouble(queryRanking.getLotSizeBias().getSize()) *
                        (listing.getLotSizeNum() / resultMetadata.getMaxLotSize());
            } else {
                score += Double.parseDouble(queryRanking.getLotSizeBias().getNa()) * score;
            }
        }
        return score;
    }

    public List<Listing> searchWarehousify(String userUID, UserQuery userQuery)
            throws ExecutionException, InterruptedException, IOException {
        HashMap<String, PointOfInterest> activePointsOfInterest = userService.getUserActivePointsOfInterest(userUID);
        List<Listing> resultListings = rangeSearchService.rangeQuerySearch(activePointsOfInterest);
        ResultMetadata resultMetadata = getResultMetadata(resultListings);
        ListIterator<Listing> iter = resultListings.listIterator();

        while (iter.hasNext()) {
            Listing listing = iter.next();

            if (filterListing(listing, userQuery.getQueryFilter())) {
                iter.remove();
            }
            listing.setListingScore(calculateScore(listing, userQuery.getQueryRanking(), resultMetadata));
        }

        resultListings.sort(Comparator.comparing(Listing::getListingScore).reversed());
        return resultListings;
    }
}
