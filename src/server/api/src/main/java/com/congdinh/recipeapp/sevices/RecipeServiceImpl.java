package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.recipeapp.dtos.category.CategoryDTO;
import com.congdinh.recipeapp.dtos.recipe.RecipeCreateDTO;
import com.congdinh.recipeapp.dtos.recipe.RecipeDTO;
import com.congdinh.recipeapp.dtos.recipe.RecipeIngredientDTO;
import com.congdinh.recipeapp.entities.Category;
import com.congdinh.recipeapp.entities.Recipe;
import com.congdinh.recipeapp.entities.RecipeIngredient;
import com.congdinh.recipeapp.entities.RecipeIngredientId;
import com.congdinh.recipeapp.repositories.IngredientRepository;
import com.congdinh.recipeapp.repositories.RecipeIngredientRepository;
import com.congdinh.recipeapp.repositories.RecipeRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
            RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    @Override
    public List<RecipeDTO> findAll() {
        // Get List of entities
        var categories = recipeRepository.findAll();

        // Convert entities to DTOs
        var recipeDTOs = categories.stream().map(recipe -> {
            var recipeDTO = new RecipeDTO();

            recipeDTO.setId(recipe.getId());
            recipeDTO.setTitle(recipe.getTitle());
            recipeDTO.setDescription(recipe.getDescription());
            recipeDTO.setImage(recipe.getImage());
            recipeDTO.setPrepTime(recipe.getPrepTime());
            recipeDTO.setCookTime(recipe.getCookTime());
            recipeDTO.setServings(recipe.getServings());

            // Check if category is not null then set categoryDTO to recipeDTO
            if (recipe.getCategory() != null) {
                recipeDTO.setCategoryId(recipe.getCategory().getId());

                // Create categoryDTO
                var categoryDTO = new CategoryDTO();
                categoryDTO.setId(recipe.getCategory().getId());
                categoryDTO.setName(recipe.getCategory().getName());

                // Set categoryDTO to recipeDTO
                recipeDTO.setCategory(categoryDTO);
            }

            return recipeDTO;
        }).toList();

        // Return DTOs
        return recipeDTOs;
    }

    @Override
    public Page<RecipeDTO> findAll(String keyword, Pageable pageable) {
        // Find recipe by keyword
        Specification<Recipe> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(title) LIKE %keyword%
            Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(description) LIKE %keyword%
            Predicate desPredicate = criteriaBuilder.like(root.get("description"),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(title) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(titlePredicate, desPredicate);
        };

        var categories = recipeRepository.findAll(specification, pageable);

        // Covert Page<Recipe> to Page<RecipeDTO>
        var recipeDTOs = categories.map(recipe -> {
            var recipeDTO = new RecipeDTO();

            recipeDTO.setId(recipe.getId());
            recipeDTO.setTitle(recipe.getTitle());
            recipeDTO.setDescription(recipe.getDescription());
            recipeDTO.setImage(recipe.getImage());
            recipeDTO.setPrepTime(recipe.getPrepTime());
            recipeDTO.setCookTime(recipe.getCookTime());
            recipeDTO.setServings(recipe.getServings());

            // Check if category is not null then set categoryDTO to recipeDTO
            if (recipe.getCategory() != null) {
                recipeDTO.setCategoryId(recipe.getCategory().getId());

                // Create categoryDTO
                var categoryDTO = new CategoryDTO();
                categoryDTO.setId(recipe.getCategory().getId());
                categoryDTO.setName(recipe.getCategory().getName());

                // Set categoryDTO to recipeDTO
                recipeDTO.setCategory(categoryDTO);
            }

            // Get ingredients of recipe and convert to DTO
            var recipeIngredientDTOs = recipe.getIngredients().stream().map(recipeIngredient -> {
                var recipeIngredientDTO = new RecipeIngredientDTO();

                recipeIngredientDTO.setIngredientId(recipeIngredient.getIngredient().getId());
                recipeIngredientDTO.setIngredientName(recipeIngredient.getIngredient().getName());
                recipeIngredientDTO.setAmount(recipeIngredient.getAmount());

                return recipeIngredientDTO;
            }).toList();

            // Set ingredients to recipeDTO
            recipeDTO.setIngredients(recipeIngredientDTOs);

            return recipeDTO;
        });

        return recipeDTOs;
    }

    @Override
    public Page<RecipeDTO> findAll(String keyword, String categoryName, Pageable pageable) {
        // Find recipe by keyword
        Specification<Recipe> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(title) LIKE %keyword%
            Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(description) LIKE %keyword%
            Predicate desPredicate = criteriaBuilder.like(root.get("description"),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(title) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            Predicate keywordPredicate = criteriaBuilder.or(titlePredicate, desPredicate);

            if (categoryName == null || categoryName.isBlank()) {
                return keywordPredicate;
            }

            // WHERE description LIKE %keyword%
            Predicate categoryNamePredicate = criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("category").get("name")),
                    categoryName.toLowerCase());

            // WHERE LOWER(title) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.and(keywordPredicate, categoryNamePredicate);
        };

        var categories = recipeRepository.findAll(specification, pageable);

        // Covert Page<Recipe> to Page<RecipeDTO>
        var recipeDTOs = categories.map(recipe -> {
            var recipeDTO = new RecipeDTO();

            recipeDTO.setId(recipe.getId());
            recipeDTO.setTitle(recipe.getTitle());
            recipeDTO.setDescription(recipe.getDescription());
            recipeDTO.setImage(recipe.getImage());
            recipeDTO.setPrepTime(recipe.getPrepTime());
            recipeDTO.setCookTime(recipe.getCookTime());
            recipeDTO.setServings(recipe.getServings());

            // Check if category is not null then set categoryDTO to recipeDTO
            if (recipe.getCategory() != null) {
                recipeDTO.setCategoryId(recipe.getCategory().getId());

                // Create categoryDTO
                var categoryDTO = new CategoryDTO();
                categoryDTO.setId(recipe.getCategory().getId());
                categoryDTO.setName(recipe.getCategory().getName());

                // Set categoryDTO to recipeDTO
                recipeDTO.setCategory(categoryDTO);
            }

            // Get ingredients of recipe and convert to DTO
            var recipeIngredientDTOs = recipe.getIngredients().stream().map(recipeIngredient -> {
                var recipeIngredientDTO = new RecipeIngredientDTO();

                recipeIngredientDTO.setIngredientId(recipeIngredient.getIngredient().getId());
                recipeIngredientDTO.setIngredientName(recipeIngredient.getIngredient().getName());
                recipeIngredientDTO.setAmount(recipeIngredient.getAmount());

                return recipeIngredientDTO;
            }).toList();

            // Set ingredients to recipeDTO
            recipeDTO.setIngredients(recipeIngredientDTOs);

            return recipeDTO;
        });

        return recipeDTOs;
    }

    @Override
    public RecipeDTO findById(UUID id) {
        // Get entity by id
        var recipe = recipeRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (recipe == null) {
            return null;
        }

        // Convert entity to DTO
        var recipeDTO = new RecipeDTO();

        recipeDTO.setId(recipe.getId());
        recipeDTO.setTitle(recipe.getTitle());
        recipeDTO.setDescription(recipe.getDescription());
        recipeDTO.setImage(recipe.getImage());
        recipeDTO.setPrepTime(recipe.getPrepTime());
        recipeDTO.setCookTime(recipe.getCookTime());
        recipeDTO.setServings(recipe.getServings());

        // Check if category is not null then set categoryDTO to recipeDTO
        if (recipe.getCategory() != null) {
            recipeDTO.setCategoryId(recipe.getCategory().getId());

            // Create categoryDTO
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());

            // Set categoryDTO to recipeDTO
            recipeDTO.setCategory(categoryDTO);
        }

        // Get ingredients of recipe and convert to DTO
        var recipeIngredientDTOs = recipe.getIngredients().stream().map(recipeIngredient -> {
            var recipeIngredientDTO = new RecipeIngredientDTO();

            recipeIngredientDTO.setIngredientId(recipeIngredient.getIngredient().getId());
            recipeIngredientDTO.setIngredientName(recipeIngredient.getIngredient().getName());
            recipeIngredientDTO.setAmount(recipeIngredient.getAmount());

            return recipeIngredientDTO;
        }).toList();

        // Set ingredients to recipeDTO
        recipeDTO.setIngredients(recipeIngredientDTOs);

        // Return DTO
        return recipeDTO;
    }

    @Override
    public RecipeDTO create(RecipeCreateDTO recipeCreateDTO) {
        // Check if recipeDTO is null then throw exception
        if (recipeCreateDTO == null) {
            throw new IllegalArgumentException("Recipe is required");
        }

        // Check if recipe with the same title exists
        var recipe = recipeRepository.findByTitle(recipeCreateDTO.getTitle());

        if (recipe != null) {
            throw new IllegalArgumentException("Recipe with the same title already exists");
        }

        // Convert DTO to entity
        recipe = new Recipe();
        recipe.setTitle(recipeCreateDTO.getTitle());
        recipe.setDescription(recipeCreateDTO.getDescription());
        recipe.setImage(recipeCreateDTO.getImage());
        recipe.setPrepTime(recipeCreateDTO.getPrepTime());
        recipe.setCookTime(recipeCreateDTO.getCookTime());
        recipe.setServings(recipeCreateDTO.getServings());

        // Check if category is not null then set category to recipe
        if (recipeCreateDTO.getCategoryId() != null) {
            var category = new Category();
            category.setId(recipeCreateDTO.getCategoryId());
            recipe.setCategory(category);
        }

        // Save entity
        recipe = recipeRepository.save(recipe);

        // Convert entity to DTO
        var recipeDTO = new RecipeDTO();
        recipeDTO.setId(recipe.getId());
        recipeDTO.setTitle(recipe.getTitle());
        recipeDTO.setDescription(recipe.getDescription());
        recipeDTO.setImage(recipe.getImage());
        recipeDTO.setPrepTime(recipe.getPrepTime());
        recipeDTO.setCookTime(recipe.getCookTime());
        recipeDTO.setServings(recipe.getServings());

        // Check if category is not null then set categoryDTO to recipeDTO
        if (recipe.getCategory() != null) {

            // Create categoryDTO
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());

            // Set categoryDTO to recipeDTO
            recipeDTO.setCategory(categoryDTO);
        }

        // Return DTO
        return recipeDTO;
    }

    @Override
    public RecipeDTO update(UUID id, RecipeDTO recipeDTO) {
        // Check if recipeDTO is null then throw exception
        if (recipeDTO == null) {
            throw new IllegalArgumentException("Recipe is required");
        }

        // Get entity by id
        var recipe = recipeRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (recipe == null) {
            return null;
        }

        // Check if recipe with the same title exists
        var recipeWithSameName = recipeRepository.findByTitle(recipeDTO.getTitle());

        if (recipeWithSameName != null && !recipeWithSameName.getId().equals(id)) {
            throw new IllegalArgumentException("Recipe with the same title already exists");
        }

        // Update entity
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setImage(recipeDTO.getImage());
        recipe.setPrepTime(recipeDTO.getPrepTime());
        recipe.setCookTime(recipeDTO.getCookTime());
        recipe.setServings(recipeDTO.getServings());

        // Check if category is not null then set category to recipe
        if (recipeDTO.getCategory() != null) {
            recipeDTO.setCategoryId(recipeDTO.getCategory().getId());

            var category = new Category();
            category.setId(recipeDTO.getCategory().getId());
            recipe.setCategory(category);
        }

        // Save entity
        recipe = recipeRepository.save(recipe);

        // Convert entity to DTO
        // Only id is needed because title and description are already in recipeDTO
        recipeDTO.setId(recipe.getId());

        // Check if category is not null then set categoryDTO to recipeDTO
        if (recipe.getCategory() != null) {
            recipeDTO.setCategoryId(recipe.getCategory().getId());

            // Create categoryDTO
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());

            // Set categoryDTO to recipeDTO
            recipeDTO.setCategory(categoryDTO);
        }

        // Return DTO
        return recipeDTO;
    }

    @Override
    public boolean deleteById(UUID id) {
        // Check if entity exists
        var recipe = recipeRepository.findById(id).orElse(null);

        // Check if entity is null then return
        if (recipe == null) {
            return false;
        }

        // Delete entity
        recipeRepository.delete(recipe);

        // Check if entity is deleted
        var isDeleted = recipeRepository.findById(id).isEmpty();

        // Return if entity is deleted
        return isDeleted;
    }

    @Override
    public boolean addIngredient(RecipeDTO recipe, RecipeIngredientDTO recipeIngredientDTO) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe is required");
        }

        if (recipeIngredientDTO == null) {
            throw new IllegalArgumentException("Recipe ingredient is required");
        }

        var recipeEntity = recipeRepository.findById(recipe.getId()).orElse(null);
        if (recipeEntity == null) {
            return false;
        }

        var ingredient = ingredientRepository.findById(recipeIngredientDTO.getIngredientId()).orElse(null);
        if (ingredient == null) {
            return false;
        }

        var recipeIngredientId = new RecipeIngredientId(recipeEntity.getId(), ingredient.getId());
        var recipeIngredient = new RecipeIngredient();
        recipeIngredient.setId(recipeIngredientId);
        recipeIngredient.setRecipe(recipeEntity);
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(recipeIngredientDTO.getAmount());

        recipeIngredient = recipeIngredientRepository.save(recipeIngredient);

        return recipeIngredient != null;
    }

    @Override
    public boolean deleteIngredient(RecipeDTO recipe, UUID ingredientId) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe is required");
        }

        var recipeEntity = recipeRepository.findById(recipe.getId()).orElse(null);
        if (recipeEntity == null) {
            return false;
        }

        var ingredient = ingredientRepository.findById(ingredientId).orElse(null);

        if (ingredient == null) {
            return false;
        }

        var recipeIngredientId = new RecipeIngredientId(recipeEntity.getId(), ingredient.getId());
        var recipeIngredient = recipeIngredientRepository.findById(recipeIngredientId).orElse(null);

        if (recipeIngredient == null) {
            return false;
        }

        recipeIngredientRepository.delete(recipeIngredient);
        
        var isDeleted = recipeIngredientRepository.findById(recipeIngredientId).isEmpty();

        return isDeleted;
    }
}
