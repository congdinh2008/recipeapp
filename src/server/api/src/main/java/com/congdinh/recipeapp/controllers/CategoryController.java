package com.congdinh.recipeapp.controllers;

import java.util.UUID;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.congdinh.recipeapp.dtos.category.*;
import com.congdinh.recipeapp.sevices.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Category API")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Return all categories")
    public ResponseEntity<Page<CategoryDTO>> index(
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

        // Search category by keyword and paging
        var categories = categoryService.findAll(keyword, pageable);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    @ApiResponse(responseCode = "200", description = "Return category by id")
    public ResponseEntity<CategoryDTO> show(@PathVariable UUID id) {
        var categoryDTO = categoryService.findById(id);

        return ResponseEntity.ok(categoryDTO);
    }

    @PostMapping()
    @Operation(summary = "Create new category")
    @ApiResponse(responseCode = "200", description = "Return new category")
    @ApiResponse(responseCode = "400", description = "Failed to create category")
    public ResponseEntity<?> create(@Valid @ModelAttribute CategoryCreateDTO categoryCreateDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var result = categoryService.create(categoryCreateDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body("Failed to create category");
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit category by id")
    @ApiResponse(responseCode = "200", description = "Return updated category")
    @ApiResponse(responseCode = "400", description = "Failed to update category")
    public ResponseEntity<?> edit(@PathVariable UUID id,
            @ModelAttribute @Valid CategoryDTO categoryDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var result = categoryService.update(id, categoryDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body("Failed to update category");
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id")
    @ApiResponse(responseCode = "200", description = "Return true if delete success")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        var result = categoryService.deleteById(id);

        return ResponseEntity.ok(result);
    }
}
