package com.congdinh.recipeapp.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.congdinh.recipeapp.dtos.recipe.RecipeIngredientDTO;
import com.congdinh.recipeapp.sevices.RecipeService;

@RestController
@RequestMapping("/api/recipes")
public class RecipeAPIController {
    private final RecipeService recipeService;

    public RecipeAPIController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/add-ingredients/{id}")
    public ResponseEntity<Boolean> addIngredients(@PathVariable UUID id,
            @RequestBody RecipeIngredientDTO recipeIngredientDTO) {
        var recipe = recipeService.findById(id);
        if (recipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        var result = recipeService.addIngredient(recipe, recipeIngredientDTO);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}/delete-ingredients/{ingredientId}")
    public ResponseEntity<Boolean> deleteIngredients(@PathVariable UUID id,
            @PathVariable UUID ingredientId) {
        var recipe = recipeService.findById(id);
        if (recipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        boolean result = recipeService.deleteIngredient(recipe, ingredientId);

        return ResponseEntity.ok(result);
    }
}