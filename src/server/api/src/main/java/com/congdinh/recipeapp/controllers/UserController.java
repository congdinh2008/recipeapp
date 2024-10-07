package com.congdinh.recipeapp.controllers;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.recipeapp.dtos.auth.UserCreateDTO;
import com.congdinh.recipeapp.dtos.auth.UserDTO;
import com.congdinh.recipeapp.dtos.messages.Message;
import com.congdinh.recipeapp.sevices.RoleService;
import com.congdinh.recipeapp.sevices.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manage/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "firstName") String sortBy, // Xac dinh truong sap xep
            @RequestParam(required = false, defaultValue = "asc") String order, // Xac dinh chieu sap xep
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            Model model) {
        Pageable pageable = null;

        if (order.equals("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        // Search user by keyword and paging
        var users = userService.findAll(keyword, pageable);
        model.addAttribute("users", users);

        // Passing keyword to view
        model.addAttribute("keyword", keyword);

        // Passing total pages to view
        model.addAttribute("totalPages", users.getTotalPages());

        // Passing total elements to view
        model.addAttribute("totalElements", users.getTotalElements());

        // Passing current sortBy to view
        model.addAttribute("sortBy", sortBy);

        // Passing current order to view
        model.addAttribute("order", order);

        // Limit page
        model.addAttribute("pageLimit", 3);

        // Passing current page to view
        model.addAttribute("page", page);

        // Passing current size to view
        model.addAttribute("pageSize", size);

        // Passing pageSizes to view
        model.addAttribute("pageSizes", new Integer[] { 10, 20, 30, 50, 100 });

        // Get message from redirect
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", new Message());
        }
        return "manage/users/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        var userCreateDTO = new UserCreateDTO();
        model.addAttribute("userCreateDTO", userCreateDTO);

        var roles = roleService.findAll();
        model.addAttribute("roles", roles);

        return "manage/users/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute UserCreateDTO userCreateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userCreateDTO", userCreateDTO);

            var roles = roleService.findAll();
            model.addAttribute("roles", roles);
            return "manage/users/create";
        }

        var result = userService.create(userCreateDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to create user");
            model.addAttribute("message", errorMessage);

            var roles = roleService.findAll();
            model.addAttribute("roles", roles);

            return "manage/users/create";
        }

        var successMessage = new Message("success", "User created successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/users";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        var userDTO = userService.findById(id);
        model.addAttribute("userDTO", userDTO);

        var roles = roleService.findAll();
        model.addAttribute("roles", roles);

        return "manage/users/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable UUID id,
            @Valid @ModelAttribute UserDTO userDTO,
            BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDTO", userDTO);
            return "manage/users/edit";
        }

        var result = userService.update(id, userDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to update user");
            model.addAttribute("message", errorMessage);

            var roles = roleService.findAll();
            model.addAttribute("roles", roles);

            return "manage/users/edit";

        }

        var successMessage = new Message("success", "User updated successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        var result = userService.deleteById(id);

        if (!result) {
            var errorMessage = new Message("error", "Failed to delete user");
            redirectAttributes.addFlashAttribute("message", errorMessage);
        } else {
            var successMessage = new Message("success", "User deleted successfully");
            redirectAttributes.addFlashAttribute("message", successMessage);
        }
        return "redirect:/manage/users";
    }
}
