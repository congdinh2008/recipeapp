package com.congdinh.recipeapp.entities;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RecipeIngredientId implements Serializable {
    private UUID recipeId;
    private UUID ingredientId;
}