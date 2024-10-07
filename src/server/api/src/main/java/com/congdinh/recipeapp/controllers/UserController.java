package com.congdinh.recipeapp.controllers;

import java.util.UUID;

import org.springframework.data.domain.*;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Return all users")
    public ResponseEntity<Page<UserDTO>> index(
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

        // Search user by keyword and paging
        var users = userService.findAll(keyword, pageable);

        return ResponseEntity.ok(users);
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
    public ResponseEntity<?> create(@Valid @ModelAttribute UserCreateDTO userCreateDTO,
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
            @Valid @ModelAttribute UserDTO userDTO,
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
