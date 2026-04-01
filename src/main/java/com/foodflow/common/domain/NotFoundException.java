package com.foodflow.common.domain;

public class NotFoundException extends DomainException {

    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }

    public NotFoundException(String resource, String identifier) {
        super("NOT_FOUND", resource + " not found: " + identifier);
    }
}
