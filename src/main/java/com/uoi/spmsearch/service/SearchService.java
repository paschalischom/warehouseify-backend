package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SearchService {

    private final UserService userService;
    private final RangeSearchService rangeSearchService;

    private boolean filterListing(Listing listing, QueryCustomization queryCustomization) {

        if (!queryCustomization.getState().equals("") &&
                !listing.getState().equals(queryCustomization.getState())) {
            return true;
        }
        if (!queryCustomization.getBuildingClass().equals("") &&
                !listing.getBuildingClass().equals(queryCustomization.getBuildingClass())) {
            if (!queryCustomization.isBuildingClassCheckbox()) {
                return true;
            }
            else if (queryCustomization.isBuildingClassCheckbox() && !listing.getBuildingClass().equals("N/A")) {
                return true;
            }
        }
        if (!queryCustomization.getPropertyType().equals("") &&
                !listing.getSubTypes().contains(queryCustomization.getPropertyType())) {
            if (!queryCustomization.isPropertyTypeCheckbox()) {
                return true;
            }
            else if (queryCustomization.isPropertyTypeCheckbox() && !listing.getSubTypes().contains("N/A")) {
                return true;
            }
        }
        if (!queryCustomization.getStatus().equals("") && !listing.getStatus().equals(queryCustomization.getStatus())) {
            return true;
        }
        if (listing.getPriceHigh() == -1) { // The listing doesn't have an upper pricing bound
            if (queryCustomization.getPriceRangeFrom() > listing.getPriceLow() ||
                    queryCustomization.getPriceRangeTo() < listing.getPriceLow()) {
                return true;
            }
        } else if (queryCustomization.getPriceRangeFrom() > listing.getPriceLow() ||
                queryCustomization.getPriceRangeTo() < listing.getPriceHigh()) {
            return true;
        }
        return false;
    }

    private double calculateScore(Listing listing, QueryRankingPreferences queryRankingPreferences) {
        double score = 0;
        for (String poiUID: queryRankingPreferences.getPoiBias().keySet()) {
            score += queryRankingPreferences.getPoiBias().get(poiUID) * listing.getDistancesToPoi().get(poiUID);
        }
        return score;
    }

    public List<Listing> searchWarehousify(String userUID, QueryCustomization queryCustomization, QueryRankingPreferences queryRankingPreferences)
            throws ExecutionException, InterruptedException, IOException {
        HashMap<String, PointOfInterest> activePointsOfInterest = userService.getUserActivePointsOfInterest(userUID);
        List<Listing> resultListings = rangeSearchService.rangeQuerySearch(activePointsOfInterest);
        ListIterator<Listing> iter = resultListings.listIterator();

        while (iter.hasNext()) {
            Listing listing = iter.next();

            if (filterListing(listing, queryCustomization)) {
                iter.remove();
            }
            listing.setListingScore(calculateScore(listing, queryRankingPreferences));
        }

        Collections.sort(
                resultListings, Comparator.comparing(Listing::getListingScore)
        );
        return resultListings;
        // return resultListings;
    }
}
