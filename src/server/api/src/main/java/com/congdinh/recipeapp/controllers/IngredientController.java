package com.congdinh.recipeapp.controllers;

import java.util.UUID;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.congdinh.recipeapp.dtos.ingredient.*;
import com.congdinh.recipeapp.sevices.IngredientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredient", description = "Ingredient API")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    @Operation(summary = "Get all ingredients")
    @ApiResponse(responseCode = "200", description = "Return all ingredients")
    public ResponseEntity<Page<IngredientDTO>> index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "name") String sortBy, // Xac dinh truong sap xep
            @RequestParam(required = false, defaultValue = "asc") String order, // Xac dinh chieu sap xep
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = null;

        if (order.equals("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        // Search ingredient by keyword and paging
        var ingredients = ingredientService.findAll(keyword, pageable);

        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ingredient by id")
    @ApiResponse(responseCode = "200", description = "Return ingredient by id")
    public ResponseEntity<IngredientDTO> show(@PathVariable UUID id) {
        var ingredientDTO = ingredientService.findById(id);

        return ResponseEntity.ok(ingredientDTO);
    }

    @PostMapping()
    @Operation(summary = "Create new ingredient")
    @ApiResponse(responseCode = "200", description = "Return new ingredient")
    @ApiResponse(responseCode = "400", description = "Return error message")
    public ResponseEntity<?> create(@Valid @ModelAttribute IngredientCreateDTO ingredientCreateDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var result = ingredientService.create(ingredientCreateDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body("Failed to create ingredient");
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit ingredient by id")
    @ApiResponse(responseCode = "200", description = "Return edited ingredient")
    @ApiResponse(responseCode = "400", description = "Return error message")
    public ResponseEntity<?> edit(@PathVariable UUID id,
            @ModelAttribute @Valid IngredientDTO ingredientDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var result = ingredientService.update(id, ingredientDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body("Failed to create ingredient");
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete ingredient by id")
    @ApiResponse(responseCode = "200", description = "Return true if delete success")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        var result = ingredientService.deleteById(id);

        return ResponseEntity.ok(result);
    }
}
