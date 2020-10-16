package com.uoi.spmsearch.errorhandling;

import com.uoi.spmsearch.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleRecourseNotFound(ResourceNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    protected ResponseEntity<Object> handleServiceUnavailable(ExternalServiceUnavailableException ex) {
        ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE, ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidGeocodingArgumentsException.class)
    protected ResponseEntity<Object> handleInvalidGeocodingArguments(InvalidGeocodingArgumentsException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex);
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
