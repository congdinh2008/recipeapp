package com.congdinh.recipeapp.controllers;

import java.util.UUID;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.congdinh.recipeapp.dtos.auth.*;
import com.congdinh.recipeapp.dtos.messages.Message;
import com.congdinh.recipeapp.sevices.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role", description = "Role API")
public class RoleController {
    private final RoleService roleService;
    private final PagedResourcesAssembler<RoleDTO> pagedResourcesAssembler;

    public RoleController(RoleService roleService, PagedResourcesAssembler<RoleDTO> pagedResourcesAssembler) {
        this.roleService = roleService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    @ApiResponse(responseCode = "200", description = "Return all roles")
    public ResponseEntity<?> index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = null;

        if (order.equals("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        var result = roleService.findAll(keyword, pageable);
        var pagedModel = pagedResourcesAssembler.toModel(result);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by id")
    @ApiResponse(responseCode = "200", description = "Return role by id")
    @ApiResponse(responseCode = "404", description = "Role not found")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        var result = roleService.findById(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Create new role")
    @ApiResponse(responseCode = "200", description = "Return new role")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public ResponseEntity<?> create(@Valid @ModelAttribute RoleCreateDTO roleCreateDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var result = roleService.create(roleCreateDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body(new Message("error", "Failed to create role"));
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit role by id")
    @ApiResponse(responseCode = "200", description = "Return edited role")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public ResponseEntity<?> edit(@PathVariable UUID id, @Valid @ModelAttribute RoleDTO roleDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var result = roleService.update(id, roleDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body(new Message("error", "Failed to update role"));
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role by id")
    @ApiResponse(responseCode = "200", description = "Return true if deleted")
    public ResponseEntity<Boolean> deleteRole(@PathVariable UUID id) {
        var result = roleService.deleteById(id);
        return ResponseEntity.ok(result);
    }
}