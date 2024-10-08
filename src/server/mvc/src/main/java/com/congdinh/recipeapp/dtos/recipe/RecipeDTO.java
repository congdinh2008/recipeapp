package com.congdinh.recipeapp.dtos.recipe;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.congdinh.recipeapp.dtos.category.CategoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private UUID id;

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

    private CategoryDTO category;

    private List<RecipeIngredientDTO> ingredients = new ArrayList<>();
}
