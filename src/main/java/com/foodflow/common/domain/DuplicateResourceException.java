package com.foodflow.common.domain;

public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message);
    }

    public DuplicateResourceException(String resource, String identifier) {
        super("DUPLICATE_RESOURCE", resource + " already exists: " + identifier);
    }
}
