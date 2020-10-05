package com.uoi.spmsearch.errorhandling;

import com.uoi.spmsearch.dto.PointOfInterest;
import com.uoi.spmsearch.dto.User;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Class clazz, String uid) {
        super(ResourceNotFoundException.createErrorMessage(clazz, uid));
    }

    private static String createErrorMessage(Class clazz, String uid) {
        if (clazz.isInstance(User.class)) {
            return "User with id {" + uid + "} was not found.";
        } else if (clazz.isInstance(PointOfInterest.class)) {
            return "Point of Interest with id {" + uid + "} was not found.";
        } else {
            return "Resource with id {" + uid + "} was not found.";
        }
    }
}
