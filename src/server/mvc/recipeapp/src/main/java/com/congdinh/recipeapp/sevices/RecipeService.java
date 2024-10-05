package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.congdinh.recipeapp.dtos.recipe.RecipeCreateDTO;
import com.congdinh.recipeapp.dtos.recipe.RecipeDTO;

public interface RecipeService {
    List<RecipeDTO> findAll();

    Page<RecipeDTO> findAll(String keyword, Pageable pageable);

    Page<RecipeDTO> findAll(String keyword, String categoryName, Pageable pageable);

    RecipeDTO findById(UUID id);

    RecipeDTO create(RecipeCreateDTO recipeCreateDTO);

    RecipeDTO update(UUID id, RecipeDTO recipeDTO);

    boolean deleteById(UUID id);
}
