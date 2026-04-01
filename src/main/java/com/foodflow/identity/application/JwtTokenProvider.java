package com.foodflow.identity.application;

public interface JwtTokenProvider {

    String generateToken(String email, Long userId);

    String getEmailFromToken(String token);

    Long getUserIdFromToken(String token);

    boolean validateToken(String token);
}
