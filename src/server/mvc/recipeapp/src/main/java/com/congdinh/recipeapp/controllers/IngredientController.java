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

import com.congdinh.recipeapp.dtos.ingredient.IngredientCreateDTO;
import com.congdinh.recipeapp.dtos.ingredient.IngredientDTO;
import com.congdinh.recipeapp.dtos.messages.Message;
import com.congdinh.recipeapp.sevices.IngredientService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manage/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "name") String sortBy, // Xac dinh truong sap xep
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

        // Search ingredient by keyword and paging
        var ingredients = ingredientService.findAll(keyword, pageable);
        model.addAttribute("ingredients", ingredients);

        // Passing keyword to view
        model.addAttribute("keyword", keyword);

        // Passing total pages to view
        model.addAttribute("totalPages", ingredients.getTotalPages());

        // Passing total elements to view
        model.addAttribute("totalElements", ingredients.getTotalElements());

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
        return "manage/ingredients/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        var ingredientCreateDTO = new IngredientCreateDTO();
        model.addAttribute("ingredientCreateDTO", ingredientCreateDTO);

        return "manage/ingredients/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute IngredientCreateDTO ingredientCreateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ingredientCreateDTO", ingredientCreateDTO);
            return "manage/ingredients/create";
        }

        var result = ingredientService.create(ingredientCreateDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to create ingredient");
            model.addAttribute("message", errorMessage);
            return "manage/ingredients/create";
        }

        var successMessage = new Message("success", "Ingredient created successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/ingredients";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        var ingredientDTO = ingredientService.findById(id);
        model.addAttribute("ingredientDTO", ingredientDTO);

        return "manage/ingredients/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable UUID id,
            @ModelAttribute @Valid IngredientDTO ingredientDTO,
            RedirectAttributes redirectAttributes,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ingredientDTO", ingredientDTO);
            return "manage/ingredients/edit";
        }

        var result = ingredientService.update(id, ingredientDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to update ingredient");
            model.addAttribute("message", errorMessage);
            return "manage/ingredients/edit";

        }

        var successMessage = new Message("success", "Ingredient updated successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/ingredients";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        var result = ingredientService.deleteById(id);

        if (!result) {
            var errorMessage = new Message("error", "Failed to delete ingredient");
            redirectAttributes.addFlashAttribute("message", errorMessage);
        } else {
            var successMessage = new Message("success", "Ingredient deleted successfully");
            redirectAttributes.addFlashAttribute("message", successMessage);
        }
        return "redirect:/manage/ingredients";
    }
}
