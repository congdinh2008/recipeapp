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
    private final PagedResourcesAssembler<IngredientDTO> pagedResourcesAssembler;

    public IngredientController(IngredientService ingredientService,
            PagedResourcesAssembler<IngredientDTO> pagedResourcesAssembler) {
        this.ingredientService = ingredientService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all ingredients")
    @ApiResponse(responseCode = "200", description = "Return all ingredients")
    public ResponseEntity<?> index(
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

        var result = ingredientService.findAll(keyword, pageable);
        var pagedModel = pagedResourcesAssembler.toModel(result);

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/search")
    @Operation(summary = "Search ingredients")
    @ApiResponse(responseCode = "200", description = "Return ingredients by search")
    public ResponseEntity<?> search(@RequestBody IngredientSearchDTO ingredientSearchDTO) {
        Pageable pageable = PageRequest.of(ingredientSearchDTO.getPage(), ingredientSearchDTO.getSize(),
                Sort.by(Sort.Direction.fromString(ingredientSearchDTO.getDirection().toString()),
                        ingredientSearchDTO.getSort()));

        var result = ingredientService.findAll(ingredientSearchDTO.getKeyword(), pageable);

        // Convert to PagedModel
        var pagedModel = pagedResourcesAssembler.toModel(result);

        // Extract content without links
        List<IngredientDTO> contentWithoutLinks = pagedModel.getContent().stream()
                .map(entityModel -> entityModel.getContent())
                .collect(Collectors.toList());

        var response = new HashMap<String, Object>();
        response.put("items", contentWithoutLinks);
        response.put("page", pagedModel.getMetadata());
        response.put("links", pagedModel.getLinks());
        return ResponseEntity.ok(response);
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
    public ResponseEntity<?> create(@Valid @RequestBody IngredientCreateDTO ingredientCreateDTO,
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
            @RequestBody @Valid IngredientDTO ingredientDTO,
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
