package com.congdinh.recipeapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.congdinh.recipeapp.dtos.auth.*;
import com.congdinh.recipeapp.dtos.messages.Message;
import com.congdinh.recipeapp.sevices.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
public class UserController {
    private final UserService userService;
    private final PagedResourcesAssembler<UserDTO> pagedResourcesAssembler;

    public UserController(UserService userService, PagedResourcesAssembler<UserDTO> pagedResourcesAssembler) {
        this.userService = userService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Return all users")
    public ResponseEntity<?> index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "firstName") String sortBy, // Xac dinh truong sap xep
            @RequestParam(required = false, defaultValue = "asc") String order, // Xac dinh chieu sap xep
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        Pageable pageable = null;

        if (order.equals("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        var result = userService.findAll(keyword, pageable);
        var pagedModel = pagedResourcesAssembler.toModel(result);

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/search")
    @Operation(summary = "Search users")
    @ApiResponse(responseCode = "200", description = "Return users by search")
    public ResponseEntity<?> search(@RequestBody UserSearchDTO userSearchDTO) {
        Pageable pageable = PageRequest.of(userSearchDTO.getPage(), userSearchDTO.getSize(),
                Sort.by(Sort.Direction.fromString(userSearchDTO.getDirection().toString()),
                        userSearchDTO.getSort()));

        var result = userService.findAll(userSearchDTO.getKeyword(), pageable);

        // Convert to PagedModel
        var pagedModel = pagedResourcesAssembler.toModel(result);

        // Extract content without links
        List<UserDTO> contentWithoutLinks = pagedModel.getContent().stream()
                .map(entityModel -> entityModel.getContent())
                .collect(Collectors.toList());

        var response = new HashMap<String, Object>();
        response.put("items", contentWithoutLinks);
        response.put("page", pagedModel.getMetadata());
        response.put("links", pagedModel.getLinks());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @ApiResponse(responseCode = "200", description = "Return user by id")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserDTO> show(@PathVariable UUID id) {
        var userDTO = userService.findById(id);

        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping
    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "200", description = "Return new user")
    @ApiResponse(responseCode = "400", description = "Return error message")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userCreateDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var result = userService.create(userCreateDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body(new Message("error", "Failed to create user"));
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit user by id")
    @ApiResponse(responseCode = "200", description = "Return edited user")
    @ApiResponse(responseCode = "400", description = "Return error message")
    public ResponseEntity<?> edit(@PathVariable UUID id,
            @Valid @RequestBody UserDTO userDTO,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var result = userService.update(id, userDTO);

        if (result == null) {
            return ResponseEntity.badRequest().body(new Message("error", "Failed to update user"));
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @ApiResponse(responseCode = "200", description = "Return true if deleted")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        var result = userService.deleteById(id);

        return ResponseEntity.ok(result);
    }
}
