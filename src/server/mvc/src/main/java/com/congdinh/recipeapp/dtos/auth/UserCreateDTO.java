package com.congdinh.recipeapp.dtos.auth;

import java.util.UUID;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    @NotBlank(message = "First name is required")
    @Length(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Length(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Length(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Length(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    @Length(min = 8, max = 20, message = "Confirm password must be between 8 and 20 characters")
    private String confirmPassword;

    @NotBlank(message = "Email is required")
    @Length(min = 6, max = 50, message = "Email must be between 6 and 50 characters")
    private String email;

    private List<UUID> roleIds;
}
