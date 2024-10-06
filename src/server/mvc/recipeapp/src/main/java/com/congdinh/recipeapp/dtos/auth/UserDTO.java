package com.congdinh.recipeapp.dtos.auth;

import java.util.UUID;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;

    @NotBlank(message = "First name is required")
    @Length(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Length(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    private String displayName;

    @NotBlank(message = "Username is required")
    @Length(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Length(min = 6, max = 50, message = "Email must be between 6 and 50 characters")
    private String email;

    public List<UUID> roleIds;

    private List<RoleDTO> roles;
}
