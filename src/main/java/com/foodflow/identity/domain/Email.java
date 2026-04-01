package com.foodflow.identity.domain;

import com.foodflow.common.domain.ValidationException;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new ValidationException("email", "Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new ValidationException("email", "Invalid email format");
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }
}
