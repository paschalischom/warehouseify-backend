package com.uoi.spmsearch.service;

import com.uoi.spmsearch.dto.Listing;
import com.uoi.spmsearch.dto.QueryFilter;
import org.springframework.stereotype.Service;

@Service
public class FilteringService {

    public boolean filterListing(Listing listing, QueryFilter queryFilter) {

        if (!queryFilter.getState().equals("") &&
                !listing.getState().equals(queryFilter.getState())) {
            return false;
        }
        if (!queryFilter.getBuildingClass().equals("") &&
                !listing.getBuildingClass().equals(queryFilter.getBuildingClass())) {
            if (!queryFilter.isBuildingClassCheckbox()) {
                return false;
            }
            else if (queryFilter.isBuildingClassCheckbox() && !listing.getBuildingClass().equals("N/A")) {
                return false;
            }
        }
        if (!queryFilter.getPropertyType().equals("") &&
                !listing.getSubTypes().contains(queryFilter.getPropertyType())) {
            if (!queryFilter.isPropertyTypeCheckbox()) {
                return false;
            }
            else if (queryFilter.isPropertyTypeCheckbox() && !listing.getSubTypes().contains("N/A")) {
                return false;
            }
        }
        if (!queryFilter.getStatus().equals("") && !listing.getStatus().equals(queryFilter.getStatus())) {
            return false;
        }
        if (listing.getPriceHigh() == -1) { // The listing doesn't have an upper pricing bound
            if (queryFilter.getPriceRangeFrom() > listing.getPriceLow() ||
                    queryFilter.getPriceRangeTo() < listing.getPriceLow()) {
                return false;
            }
        } else if (queryFilter.getPriceRangeFrom() > listing.getPriceLow() ||
                queryFilter.getPriceRangeTo() < listing.getPriceHigh()) {
            return false;
        }
        return true;
    }

}
