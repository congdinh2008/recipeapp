package com.congdinh.recipeapp.controllers;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.recipeapp.dtos.category.CategoryCreateDTO;
import com.congdinh.recipeapp.dtos.category.CategoryDTO;
import com.congdinh.recipeapp.dtos.messages.Message;
import com.congdinh.recipeapp.sevices.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manage/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String index(Model model) {
        var categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        // Get message from redirect
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", new Message());
        }
        return "manage/categories/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        var categoryCreateDTO = new CategoryCreateDTO();
        model.addAttribute("categoryCreateDTO", categoryCreateDTO);

        return "manage/categories/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute CategoryCreateDTO categoryCreateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryCreateDTO", categoryCreateDTO);
            return "manage/categories/create";
        }

        var result = categoryService.create(categoryCreateDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to create category");
            model.addAttribute("message", errorMessage);
            return "manage/categories/create";
        }

        var successMessage = new Message("success", "Category created successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        var categoryDTO = categoryService.findById(id);
        model.addAttribute("categoryDTO", categoryDTO);

        return "manage/categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable UUID id,
            @ModelAttribute @Valid CategoryDTO categoryDTO,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryDTO", categoryDTO);
            return "manage/categories/edit";
        }

        var result = categoryService.update(id, categoryDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to update category");
            model.addAttribute("message", errorMessage);
            return "manage/categories/edit";

        }

        var successMessage = new Message("success", "Category updated successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        var result = categoryService.deleteById(id);

        if (!result) {
            var errorMessage = new Message("error", "Failed to delete category");
            redirectAttributes.addFlashAttribute("message", errorMessage);
        } else {
            var successMessage = new Message("success", "Category deleted successfully");
            redirectAttributes.addFlashAttribute("message", successMessage);
        }
        return "redirect:/manage/categories";
    }
}
