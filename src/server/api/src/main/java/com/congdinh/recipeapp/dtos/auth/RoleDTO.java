package com.congdinh.recipeapp.dtos.auth;

import java.util.UUID;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private UUID id;

    @NotBlank(message = "Name is required")
    @Length(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Length(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
