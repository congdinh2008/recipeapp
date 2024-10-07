package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.recipeapp.dtos.category.CategoryCreateDTO;
import com.congdinh.recipeapp.dtos.category.CategoryDTO;
import com.congdinh.recipeapp.entities.Category;
import com.congdinh.recipeapp.repositories.CategoryRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> findAll() {
        // Get List of entities
        var categories = categoryRepository.findAll();

        // Convert entities to DTOs
        var categoryDTOs = categories.stream().map(category -> {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            return categoryDTO;
        }).toList();

        // Return DTOs
        return categoryDTOs;
    }

     @Override
    public Page<CategoryDTO> findAll(String keyword, Pageable pageable) {
         // Find category by keyword
         Specification<Category> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(name) LIKE %keyword%
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(description) LIKE %keyword%
            Predicate desPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(namePredicate, desPredicate);
        };

        var categories = categoryRepository.findAll(specification, pageable);

        // Covert Page<Category> to Page<CategoryDTO>
        var categoryDTOs = categories.map(category -> {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            return categoryDTO;
        });

        return categoryDTOs;
    }

    @Override
    public CategoryDTO findById(UUID id) {
        // Get entity by id
        var category = categoryRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (category == null) {
            return null;
        }

        // Convert entity to DTO
        var categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());

        // Return DTO
        return categoryDTO;
    }

    @Override
    public CategoryDTO create(CategoryCreateDTO categoryCreateDTO) {
        // Check if categoryDTO is null then throw exception
        if (categoryCreateDTO == null) {
            throw new IllegalArgumentException("Category is required");
        }

        // Check if category with the same name exists
        var category = categoryRepository.findByName(categoryCreateDTO.getName());

        if (category != null) {
            throw new IllegalArgumentException("Category with the same name already exists");
        }

        // Convert DTO to entity
        category = new Category();
        category.setName(categoryCreateDTO.getName());
        category.setDescription(categoryCreateDTO.getDescription());

        // Save entity
        category = categoryRepository.save(category);

        // Convert entity to DTO
        var categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());

        // Return DTO
        return categoryDTO;
    }

    @Override
    public CategoryDTO update(UUID id, CategoryDTO categoryDTO) {
        // Check if categoryDTO is null then throw exception
        if (categoryDTO == null) {
            throw new IllegalArgumentException("Category is required");
        }

        // Get entity by id
        var category = categoryRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (category == null) {
            return null;
        }

        // Check if category with the same name exists
        var categoryWithSameName = categoryRepository.findByName(categoryDTO.getName());

        if (categoryWithSameName != null && !categoryWithSameName.getId().equals(id)) {
            throw new IllegalArgumentException("Category with the same name already exists");
        }

        // Update entity
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        // Save entity
        category = categoryRepository.save(category);

        // Convert entity to DTO
        // Only id is needed because name and description are already in categoryDTO
        categoryDTO.setId(category.getId());

        // Return DTO
        return categoryDTO;
    }

    @Override
    public boolean deleteById(UUID id) {
        // Check if entity exists
        var category = categoryRepository.findById(id).orElse(null);

        // Check if entity is null then return
        if (category == null) {
            return false;
        }

        // Delete entity
        categoryRepository.delete(category);

        // Check if entity is deleted
        var isDeleted = categoryRepository.findById(id).isEmpty();

        // Return if entity is deleted
        return isDeleted;
    }
}
