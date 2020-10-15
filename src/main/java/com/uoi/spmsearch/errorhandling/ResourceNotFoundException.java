package com.uoi.spmsearch.errorhandling;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Class clazz, String uid) {
        super(ResourceNotFoundException.createErrorMessage(clazz, uid));
    }

    private static String createErrorMessage(Class clazz, String uid) {
        return clazz.getSimpleName() + " resource with id {" + uid + "} was not found.";
    }
}
