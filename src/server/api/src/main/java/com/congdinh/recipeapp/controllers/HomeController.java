package com.congdinh.recipeapp.controllers;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.congdinh.recipeapp.sevices.RecipeService;

@Controller
@RequestMapping("/")
public class HomeController {
    private final RecipeService recipeService;

    public HomeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    @GetMapping
    public String index(Model model) {
        var recipes = recipeService.findAll();
        model.addAttribute("recipes", recipes);
        return "home/index";
    }

    @GetMapping("/recipes/{id}")
    public String showRecipe(@PathVariable UUID id, Model model) {
        var recipe = recipeService.findById(id);
        model.addAttribute("recipe", recipe);
        return "home/recipe";
    }

    @GetMapping("about")
    public String about() {
        return "home/about";
    }

    @GetMapping("contact")
    public String contact() {
        return "home/contact";
    }
}
