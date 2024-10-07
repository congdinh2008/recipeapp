package com.congdinh.recipeapp.dtos.recipe;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientDTO {

    @NotNull(message = "Ingredient is required")
    private UUID ingredientId;

    private String ingredientName;

    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Amount can't be negative")
    private String amount;
}
