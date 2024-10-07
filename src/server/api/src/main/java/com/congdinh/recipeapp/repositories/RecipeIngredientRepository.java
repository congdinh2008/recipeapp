package com.congdinh.recipeapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.congdinh.recipeapp.entities.RecipeIngredient;
import com.congdinh.recipeapp.entities.RecipeIngredientId;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId> {
}
