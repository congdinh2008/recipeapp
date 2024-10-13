package com.congdinh.recipeapp.controllers;

import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
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
    private final PagedResourcesAssembler<RecipeDTO> pagedResourcesAssembler;

    public RecipeController(RecipeService recipeService, PagedResourcesAssembler<RecipeDTO> pagedResourcesAssembler) {
        this.recipeService = recipeService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all recipes")
    @ApiResponse(responseCode = "200", description = "Return all recipes")
    public ResponseEntity<?> index(
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

        var result = recipeService.findAll(keyword, pageable);
        var pagedModel = pagedResourcesAssembler.toModel(result);

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/search")
    @Operation(summary = "Search recipes")
    @ApiResponse(responseCode = "200", description = "Return recipes by search")
    public ResponseEntity<?> search(@RequestBody RecipeSearchDTO recipeSearchDTO) {
        Pageable pageable = PageRequest.of(recipeSearchDTO.getPage(), recipeSearchDTO.getSize(),
                Sort.by(Sort.Direction.fromString(recipeSearchDTO.getDirection().toString()),
                        recipeSearchDTO.getSort()));

        var result = recipeService.findAll(recipeSearchDTO.getKeyword(), pageable);

        // Convert to PagedModel
        var pagedModel = pagedResourcesAssembler.toModel(result);

        // Extract content without links
        List<RecipeDTO> contentWithoutLinks = pagedModel.getContent().stream()
                .map(entityModel -> entityModel.getContent())
                .collect(Collectors.toList());

        var response = new HashMap<String, Object>();
        response.put("items", contentWithoutLinks);
        response.put("page", pagedModel.getMetadata());
        response.put("links", pagedModel.getLinks());
        return ResponseEntity.ok(response);
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
    public ResponseEntity<?> create(@RequestBody @Valid RecipeCreateDTO recipeCreateDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
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
            @RequestBody @Valid RecipeDTO recipeDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
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
