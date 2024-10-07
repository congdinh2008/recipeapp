package com.congdinh.recipeapp.sevices;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateAccessToken(Authentication authentication);

    Authentication getAuthentication(String jwtToken);
}
