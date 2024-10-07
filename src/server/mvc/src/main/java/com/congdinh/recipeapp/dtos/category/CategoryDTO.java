package com.congdinh.recipeapp.dtos.category;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private UUID id;

    @NotBlank(message = "Name is required")
    @Length(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Length(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
