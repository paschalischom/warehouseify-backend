package com.uoi.spmsearch.errorhandling;

public class ExternalServiceUnavailableException extends RuntimeException {

    public ExternalServiceUnavailableException(String googleError) {
        super(ExternalServiceUnavailableException.createErrorMessage(googleError));
    }

    private static String createErrorMessage(String googleError) {
        return "Server encountered an unavailable service. Response was: " + googleError;
    }
}
