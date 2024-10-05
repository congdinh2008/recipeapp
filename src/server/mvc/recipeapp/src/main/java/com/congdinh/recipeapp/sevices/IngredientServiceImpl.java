package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.recipeapp.dtos.ingredient.IngredientCreateDTO;
import com.congdinh.recipeapp.dtos.ingredient.IngredientDTO;
import com.congdinh.recipeapp.entities.Ingredient;
import com.congdinh.recipeapp.repositories.IngredientRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
public class IngredientServiceImpl implements IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public List<IngredientDTO> findAll() {
        // Get List of entities
        var ingredients = ingredientRepository.findAll();

        // Convert entities to DTOs
        var ingredientDTOs = ingredients.stream().map(ingredient -> {
            var ingredientDTO = new IngredientDTO();
            ingredientDTO.setId(ingredient.getId());
            ingredientDTO.setName(ingredient.getName());
            return ingredientDTO;
        }).toList();

        // Return DTOs
        return ingredientDTOs;
    }

    @Override
    public Page<IngredientDTO> findAll(String keyword, Pageable pageable) {
        // Find ingredient by keyword
        Specification<Ingredient> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // WHERE LOWER(name) LIKE %keyword%
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%");
        };

        var ingredients = ingredientRepository.findAll(specification, pageable);

        // Covert Page<Ingredient> to Page<IngredientDTO>
        var ingredientDTOs = ingredients.map(ingredient -> {
            var ingredientDTO = new IngredientDTO();
            ingredientDTO.setId(ingredient.getId());
            ingredientDTO.setName(ingredient.getName());
            return ingredientDTO;
        });

        return ingredientDTOs;
    }

    @Override
    public IngredientDTO findById(UUID id) {
        // Get entity by id
        var ingredient = ingredientRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (ingredient == null) {
            return null;
        }

        // Convert entity to DTO
        var ingredientDTO = new IngredientDTO();
        ingredientDTO.setId(ingredient.getId());
        ingredientDTO.setName(ingredient.getName());

        // Return DTO
        return ingredientDTO;
    }

    @Override
    public IngredientDTO create(IngredientCreateDTO ingredientCreateDTO) {
        // Check if ingredientDTO is null then throw exception
        if (ingredientCreateDTO == null) {
            throw new IllegalArgumentException("Ingredient is required");
        }

        // Check if ingredient with the same name exists
        var ingredient = ingredientRepository.findByName(ingredientCreateDTO.getName());

        if (ingredient != null) {
            throw new IllegalArgumentException("Ingredient with the same name already exists");
        }

        // Convert DTO to entity
        ingredient = new Ingredient();
        ingredient.setName(ingredientCreateDTO.getName());

        // Save entity
        ingredient = ingredientRepository.save(ingredient);

        // Convert entity to DTO
        var ingredientDTO = new IngredientDTO();
        ingredientDTO.setId(ingredient.getId());
        ingredientDTO.setName(ingredient.getName());

        // Return DTO
        return ingredientDTO;
    }

    @Override
    public IngredientDTO update(UUID id, IngredientDTO ingredientDTO) {
        // Check if ingredientDTO is null then throw exception
        if (ingredientDTO == null) {
            throw new IllegalArgumentException("Ingredient is required");
        }

        // Get entity by id
        var ingredient = ingredientRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (ingredient == null) {
            return null;
        }

        // Check if ingredient with the same name exists
        var ingredientWithSameName = ingredientRepository.findByName(ingredientDTO.getName());

        if (ingredientWithSameName != null && !ingredientWithSameName.getId().equals(id)) {
            throw new IllegalArgumentException("Ingredient with the same name already exists");
        }

        // Update entity
        ingredient.setName(ingredientDTO.getName());

        // Save entity
        ingredient = ingredientRepository.save(ingredient);

        // Convert entity to DTO
        // Only id is needed because name and description are already in ingredientDTO
        ingredientDTO.setId(ingredient.getId());

        // Return DTO
        return ingredientDTO;
    }

    @Override
    public boolean deleteById(UUID id) {
        // Check if entity exists
        var ingredient = ingredientRepository.findById(id).orElse(null);

        // Check if entity is null then return
        if (ingredient == null) {
            return false;
        }

        // Delete entity
        ingredientRepository.delete(ingredient);

        // Check if entity is deleted
        var isDeleted = ingredientRepository.findById(id).isEmpty();

        // Return if entity is deleted
        return isDeleted;
    }
}
