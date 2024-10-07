package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.congdinh.recipeapp.dtos.ingredient.IngredientCreateDTO;
import com.congdinh.recipeapp.dtos.ingredient.IngredientDTO;

public interface IngredientService {
    List<IngredientDTO> findAll();

    Page<IngredientDTO> findAll(String keyword, Pageable pageable);

    IngredientDTO findById(UUID id);

    IngredientDTO create(IngredientCreateDTO ingredientCreateDTO);

    IngredientDTO update(UUID id, IngredientDTO ingredientDTO);

    boolean deleteById(UUID id);
}
