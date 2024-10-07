package com.congdinh.recipeapp.sevices;

import java.util.UUID;

import com.congdinh.recipeapp.dtos.auth.RegisterDTO;

public interface AuthService {
    UUID save(RegisterDTO registerDTO); // Register user + pasword encryption

    boolean existsByUsername(String username); // Check if username exists
}
