package com.congdinh.recipeapp.controllers;

import java.nio.file.*;
import java.time.*;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.recipeapp.dtos.recipe.*;
import com.congdinh.recipeapp.dtos.messages.Message;
import com.congdinh.recipeapp.sevices.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipe", description = "Recipe API")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    @Operation(summary = "Get all recipes")
    @ApiResponse(responseCode = "200", description = "Return all recipes")
    public ResponseEntity<Page<RecipeDTO>> index(
            @RequestParam(required = false) String keyword,
            @RequestParam(name = "categoryName", required = false) String categoryName,
            @RequestParam(required = false, defaultValue = "title") String sortBy, // Xac dinh truong sap xep
            @RequestParam(required = false, defaultValue = "asc") String order, // Xac dinh chieu sap xep
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = null;

        if (order.equals("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        // Search recipe by keyword and paging
        var recipes = recipeService.findAll(keyword, categoryName, pageable);

        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by id")
    @ApiResponse(responseCode = "200", description = "Return recipe by id")
    @ApiResponse(responseCode = "404", description = "recipe not found")
    public ResponseEntity<RecipeDTO> show(@PathVariable UUID id) {
        var result = recipeService.findById(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Create new recipe")
    @ApiResponse(responseCode = "200", description = "Return new recipe")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public ResponseEntity<?> create(@ModelAttribute @Valid RecipeCreateDTO recipeCreateDTO,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] bytes = imageFile.getBytes();
                // Create folder if not exist following format:
                // src/main/resources/static/images/recipes/year/month/day
                LocalDateTime date = LocalDateTime.now();
                Path folder = Paths.get("src/main/resources/static/images/recipes/" + date.getYear() + "/"
                        + date.getMonthValue() + "/" + date.getDayOfMonth());
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
                // Create file name following format: originalFileName + epochTime + extension
                String originalFileName = imageFile.getOriginalFilename();
                // Convert date to string epoch time
                Long epochTime = Instant.now().getEpochSecond();
                String fileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + "-" + epochTime
                        + originalFileName.substring(originalFileName.lastIndexOf("."));
                Path path = Paths.get(folder.toString(), fileName);
                Files.write(path, bytes);
                recipeCreateDTO.setImage(folder.toString().replace("src/main/resources/static", "") + "/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(new Message("error", "Failed to upload image"));
            }
        }

        var result = recipeService.create(recipeCreateDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body(new Message("error", "Failed to create recipe"));
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit recipe by id")
    @ApiResponse(responseCode = "200", description = "Return edited recipe")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public ResponseEntity<?> edit(@PathVariable UUID id,
            @ModelAttribute @Valid RecipeDTO recipeDTO,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var oldRecipe = recipeService.findById(id);

        if (imageFile.getOriginalFilename().isEmpty()) {
            recipeDTO.setImage(oldRecipe.getImage());
        } else {
            try {
                byte[] bytes = imageFile.getBytes();
                // Create folder if not exist following format:
                // src/main/resources/static/images/recipes/year/month/day
                LocalDateTime date = LocalDateTime.now();
                Path folder = Paths.get("src/main/resources/static/images/recipes/" + date.getYear() + "/"
                        + date.getMonthValue() + "/" + date.getDayOfMonth());
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
                // Create file name following format: originalFileName + epochTime + extension
                String originalFileName = imageFile.getOriginalFilename();
                // Convert date to string epoch time
                Long epochTime = Instant.now().getEpochSecond();
                String fileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + "-" + epochTime
                        + originalFileName.substring(originalFileName.lastIndexOf("."));
                Path path = Paths.get(folder.toString(), fileName);
                Files.write(path, bytes);
                recipeDTO.setImage(folder.toString().replace("src/main/resources/static", "") + "/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(new Message("error", "Failed to upload image"));
            }
        }

        var result = recipeService.update(id, recipeDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body(new Message("error", "Failed to update recipe"));
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        var result = recipeService.deleteById(id);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/ingredients")
    @Operation(summary = "Add ingredient to recipe")
    @ApiResponse(responseCode = "200", description = "Return true if add success")
    @ApiResponse(responseCode = "404", description = "Return false if recipe not found")
    public ResponseEntity<Boolean> addIngredients(@PathVariable UUID id,
            @RequestBody RecipeIngredientDTO recipeIngredientDTO) {
        var recipe = recipeService.findById(id);
        if (recipe == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        var result = recipeService.addIngredient(recipe, recipeIngredientDTO);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}/ingredients/{ingredientId}")
    @Operation(summary = "Delete ingredient by id")
    @ApiResponse(responseCode = "200", description = "Return true if delete success")
    @ApiResponse(responseCode = "404", description = "Return false if recipe not found")
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
