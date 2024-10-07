package com.congdinh.recipeapp.dtos.recipe;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDTO {
    @NotBlank(message = "Title is required")
    @Length(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Length(min = 3, message = "Description must be at least 3 characters")
    private String description;

    @Length(max = 255, message = "Image must be less than 255 characters")
    private String image;

    @NotNull(message = "Prep time is required")
    @PositiveOrZero(message = "Prep time can't be negative")
    private Integer prepTime = 0;

    @NotNull(message = "Cook time is required")
    @PositiveOrZero(message = "Cook time can't be negative")
    private Integer cookTime = 0;

    @NotNull(message = "Servings is required")
    @PositiveOrZero(message = "Servings can't be negative")
    private Integer servings = 0;

    @NotNull(message = "Category is required")
    private UUID categoryId;
}
