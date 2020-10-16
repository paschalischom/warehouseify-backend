package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.Listing;
import com.uoi.spmsearch.dto.QueryRanking;
import com.uoi.spmsearch.model.ResultMetadata;
import org.springframework.stereotype.Service;

@Service
public class RankingService {

    public double calculateScore(Listing listing, QueryRanking queryRanking, ResultMetadata resultMetadata) {
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
}
