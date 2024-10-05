package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;

import com.congdinh.recipeapp.dtos.category.CategoryCreateDTO;
import com.congdinh.recipeapp.dtos.category.CategoryDTO;

public interface CategoryService {
    List<CategoryDTO> findAll();

    CategoryDTO findById(UUID id);

    CategoryDTO create(CategoryCreateDTO categoryCreateDTO);

    CategoryDTO update(UUID id, CategoryDTO categoryDTO);

    boolean deleteById(UUID id);
}
