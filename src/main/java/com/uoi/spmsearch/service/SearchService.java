package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.*;
import com.uoi.spmsearch.model.ResultMetadata;
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
    private final FilteringService filteringService;
    private final RankingService rankingService;

    private ResultMetadata getResultMetadata(List<Listing> resultListings) {
        int maxDistance = 0;
        double maxSalePrice = 0;
        double maxLeasePrice = 0;
        double maxBuildingSize = 0;
        double maxLotSize = 0;

        for (Listing listing : resultListings) {
            for (DistanceDTO listingDistanceToPoi : listing.getDistancesToPoi().values()) {
                if (listingDistanceToPoi.getDistance() > maxDistance) {
                    maxDistance = listingDistanceToPoi.getDistance();
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

    public List<Listing> searchWarehousify(String userUID, UserQuery userQuery)
            throws ExecutionException, InterruptedException, IOException {
        HashMap<String, PointOfInterest> activePointsOfInterest = userService.getUserActivePointsOfInterest(userUID);
        if (activePointsOfInterest.isEmpty()) {
            return new ArrayList<>();
        }
        List<Listing> resultListings = rangeSearchService.rangeQuerySearch(activePointsOfInterest);

        List<Listing> filteredResults = resultListings.stream()
                                                      .filter(listing -> filteringService.filterListing(listing, userQuery.getQueryFilter()))
                                                      .collect(Collectors.toList());
        // We are careful to only get the metadata from actual results
        ResultMetadata resultMetadata = getResultMetadata(filteredResults);
        filteredResults.forEach(listing ->
                listing.setListingScore(
                        rankingService.calculateScore(listing, userQuery.getQueryRanking(), resultMetadata)
                )
        );

        filteredResults.sort(Comparator.comparing(Listing::getListingScore).reversed());
        return filteredResults;
    }
}
