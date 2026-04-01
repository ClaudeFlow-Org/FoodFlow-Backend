package com.foodflow.identity.domain;

public record Password(String value) {

    private static final int MIN_LENGTH = 6;

    public Password {
        if (value == null || value.isBlank()) {
            throw new ValidationException("password", "Password cannot be empty");
        }
        if (value.length() < MIN_LENGTH) {
            throw new ValidationException("password",
                    "Password must be at least " + MIN_LENGTH + " characters");
        }
    }

    public static Password of(String value) {
        return new Password(value);
    }
}
