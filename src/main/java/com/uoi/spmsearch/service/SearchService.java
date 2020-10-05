package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.*;
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
    private final FirestoreService firestoreService;

    private List<Listing> filterListings(List<Listing> resultListings, QueryCustomization queryCustomization) {
        ListIterator<Listing> iterator = resultListings.listIterator();

        while (iterator.hasNext()) {
            Listing currentListing = iterator.next();

            if (!queryCustomization.getState().equals("") &&
                    !currentListing.getState().equals(queryCustomization.getState())) {
                iterator.remove();
                continue;
            }
            if (!queryCustomization.getBuildingClass().equals("") &&
                    !currentListing.getBuildingClass().equals(queryCustomization.getBuildingClass())) {
                if (!queryCustomization.isBuildingClassCheckbox()) {
                    iterator.remove();
                    continue;
                }
                else if (queryCustomization.isBuildingClassCheckbox() && !currentListing.getBuildingClass().equals("N/A")) {
                    iterator.remove();
                    continue;
                }
            }
            if (!queryCustomization.getPropertyType().equals("") &&
                    !currentListing.getSubTypes().contains(queryCustomization.getPropertyType())) {
                if (!queryCustomization.isPropertyTypeCheckbox()) {
                    iterator.remove();
                    continue;
                }
                else if (queryCustomization.isPropertyTypeCheckbox() && !currentListing.getSubTypes().contains("N/A")) {
                    iterator.remove();
                    continue;
                }
            }
            if (!queryCustomization.getStatus().equals("") && !currentListing.getStatus().equals(queryCustomization.getStatus())) {
                iterator.remove();
                continue;
            }
            if (currentListing.getPriceHigh() == -1) { // The listing has a standard, set price
                if (queryCustomization.getPriceRangeFrom() > currentListing.getPriceLow() ||
                        queryCustomization.getPriceRangeTo() < currentListing.getPriceLow()) {
                    iterator.remove();
                    continue;
                }
            } else if (queryCustomization.getPriceRangeFrom() > currentListing.getPriceLow() ||
                    queryCustomization.getPriceRangeTo() < currentListing.getPriceHigh()) {
                iterator.remove();
                continue;
            }
        }
        return resultListings;
    }

    private List<Listing> fetchListings(List<String> listingUIDs) throws ExecutionException, InterruptedException {
        List<Listing> listings = new ArrayList<>();
        for (String listingUID: listingUIDs) {
            listings.add(firestoreService.readListingToObject(listingUID));
        }
        return listings;
    }


    public List<Listing> searchWarehousify(String userUID, QueryCustomization queryCustomization) throws ExecutionException, InterruptedException, IOException {
        HashMap<String, PointOfInterest> activePointsOfInterest = userService.getUserActivePointsOfInterest(userUID);
        List<String> resultIds = rangeSearchService.rangeQuerySearch(activePointsOfInterest);

        List<Listing> resultListings = fetchListings(resultIds);

        return filterListings(resultListings, queryCustomization);
        // return resultListings;
    }
}
