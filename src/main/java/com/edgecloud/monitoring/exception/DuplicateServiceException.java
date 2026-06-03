package com.edgecloud.monitoring.exception;

public class DuplicateServiceException extends RuntimeException {

    public DuplicateServiceException(String message) {
        super(message);
    }
}