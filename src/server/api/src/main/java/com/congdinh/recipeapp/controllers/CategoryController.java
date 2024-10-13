package com.congdinh.recipeapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
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
    private final PagedResourcesAssembler<CategoryDTO> pagedResourcesAssembler;

    public CategoryController(CategoryService categoryService,
            PagedResourcesAssembler<CategoryDTO> pagedResourcesAssembler) {
        this.categoryService = categoryService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "Return all categories")
    public ResponseEntity<?> getAll() {
        var result = categoryService.findAll();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @Operation(summary = "Search categories")
    @ApiResponse(responseCode = "200", description = "Return categories by search")
    public ResponseEntity<?> search(@RequestBody CategorySearchDTO categorySearchDTO) {
        Pageable pageable = PageRequest.of(categorySearchDTO.getPage(), categorySearchDTO.getSize(),
                Sort.by(Sort.Direction.fromString(categorySearchDTO.getDirection().toString()),
                        categorySearchDTO.getSort()));

        var result = categoryService.findAll(categorySearchDTO.getKeyword(), pageable);

        // Convert to PagedModel
        var pagedModel = pagedResourcesAssembler.toModel(result);

        // Extract content without links
        List<CategoryDTO> contentWithoutLinks = pagedModel.getContent().stream()
                .map(entityModel -> entityModel.getContent())
                .collect(Collectors.toList());

        var response = new HashMap<String, Object>();
        response.put("items", contentWithoutLinks);
        response.put("page", pagedModel.getMetadata());
        response.put("links", pagedModel.getLinks());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    @ApiResponse(responseCode = "200", description = "Return category by id")
    public ResponseEntity<CategoryDTO> getById(@PathVariable UUID id) {
        var categoryDTO = categoryService.findById(id);

        return ResponseEntity.ok(categoryDTO);
    }

    @PostMapping()
    @Operation(summary = "Create new category")
    @ApiResponse(responseCode = "200", description = "Return new category")
    @ApiResponse(responseCode = "400", description = "Failed to create category")
    public ResponseEntity<?> create(@Valid @RequestBody CategoryCreateDTO categoryCreateDTO,
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
            @RequestBody @Valid CategoryDTO categoryDTO,
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
