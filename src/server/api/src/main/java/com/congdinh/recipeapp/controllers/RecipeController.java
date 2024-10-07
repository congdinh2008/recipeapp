package com.congdinh.recipeapp.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.recipeapp.dtos.recipe.RecipeCreateDTO;
import com.congdinh.recipeapp.dtos.recipe.RecipeDTO;
import com.congdinh.recipeapp.dtos.messages.Message;
import com.congdinh.recipeapp.sevices.CategoryService;
import com.congdinh.recipeapp.sevices.IngredientService;
import com.congdinh.recipeapp.sevices.RecipeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manage/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final CategoryService categoryService;
    private final IngredientService ingredientService;

    public RecipeController(RecipeService recipeService,
            CategoryService categoryService,
            IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.categoryService = categoryService;
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(name = "categoryName", required = false) String categoryName,
            @RequestParam(required = false, defaultValue = "title") String sortBy, // Xac dinh truong sap xep
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

        // Search recipe by keyword and paging
        var recipes = recipeService.findAll(keyword, categoryName, pageable);
        model.addAttribute("recipes", recipes);

        // Passing keyword to view
        model.addAttribute("keyword", keyword);

        // Passing total pages to view
        model.addAttribute("totalPages", recipes.getTotalPages());

        // Passing total elements to view
        model.addAttribute("totalElements", recipes.getTotalElements());

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

        var categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        // Get message from redirect
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", new Message());
        }
        return "manage/recipes/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        var recipeCreateDTO = new RecipeCreateDTO();
        model.addAttribute("recipeCreateDTO", recipeCreateDTO);

        var categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        return "manage/recipes/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute @Valid RecipeCreateDTO recipeCreateDTO,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("recipeCreateDTO", recipeCreateDTO);

            var categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            return "manage/recipes/create";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] bytes = imageFile.getBytes();
                // Create folder if not exist following format:
                // src/main/resources/static/images/recipes/year/month/day
                LocalDateTime date = LocalDateTime.now();
                Path folder = Paths.get("src/main/resources/static/images/recipes/" + date.getYear() + "/"
                        + date.getMonthValue() + "/" + date.getDayOfMonth());
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
                // Create file name following format: originalFileName + epochTime + extension
                String originalFileName = imageFile.getOriginalFilename();
                // Convert date to string epoch time
                Long epochTime = Instant.now().getEpochSecond();
                String fileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + "-" + epochTime
                        + originalFileName.substring(originalFileName.lastIndexOf("."));
                Path path = Paths.get(folder.toString(), fileName);
                Files.write(path, bytes);
                recipeCreateDTO.setImage(folder.toString().replace("src/main/resources/static", "") + "/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                Message errorMessage = new Message("error", "Failed to upload image");
                model.addAttribute("message", errorMessage);
                var categories = categoryService.findAll();
                model.addAttribute("categories", categories);

                bindingResult.rejectValue("image", "image", "Failed to upload image");
                return "manage/recipes/create";
            }
        }

        var result = recipeService.create(recipeCreateDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to create recipe");
            model.addAttribute("message", errorMessage);

            var categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            return "manage/recipes/create";
        }

        var successMessage = new Message("success", "Recipe created successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/recipes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        var recipeDTO = recipeService.findById(id);
        model.addAttribute("recipeDTO", recipeDTO);

        var categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        var ingredients = ingredientService.findAll();
        model.addAttribute("ingredients", ingredients);

        return "manage/recipes/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable UUID id,
            @ModelAttribute @Valid RecipeDTO recipeDTO,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("recipeDTO", recipeDTO);

            var categories = categoryService.findAll();
            model.addAttribute("categories", categories);

            var ingredients = ingredientService.findAll();
            model.addAttribute("ingredients", ingredients);

            return "manage/recipes/edit";
        }

        var oldRecipe = recipeService.findById(id);

        if (imageFile.getOriginalFilename().isEmpty()) {
            recipeDTO.setImage(oldRecipe.getImage());
        } else {
            try {
                byte[] bytes = imageFile.getBytes();
                // Create folder if not exist following format:
                // src/main/resources/static/images/recipes/year/month/day
                LocalDateTime date = LocalDateTime.now();
                Path folder = Paths.get("src/main/resources/static/images/recipes/" + date.getYear() + "/"
                        + date.getMonthValue() + "/" + date.getDayOfMonth());
                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
                // Create file name following format: originalFileName + epochTime + extension
                String originalFileName = imageFile.getOriginalFilename();
                // Convert date to string epoch time
                Long epochTime = Instant.now().getEpochSecond();
                String fileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + "-" + epochTime
                        + originalFileName.substring(originalFileName.lastIndexOf("."));
                Path path = Paths.get(folder.toString(), fileName);
                Files.write(path, bytes);
                recipeDTO.setImage(folder.toString().replace("src/main/resources/static", "") + "/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                Message errorMessage = new Message("error", "Failed to upload image");
                model.addAttribute("message", errorMessage);

                var categories = categoryService.findAll();
                model.addAttribute("categories", categories);

                var ingredients = ingredientService.findAll();
                model.addAttribute("ingredients", ingredients);

                bindingResult.rejectValue("image", "image", "Failed to upload image");
                return "manage/recipes/edit";
            }
        }

        var result = recipeService.update(id, recipeDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to update recipe");
            model.addAttribute("message", errorMessage);

            var categories = categoryService.findAll();
            model.addAttribute("categories", categories);

            var ingredients = ingredientService.findAll();
            model.addAttribute("ingredients", ingredients);

            return "manage/recipes/edit";

        }

        var successMessage = new Message("success", "Recipe updated successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/recipes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        var result = recipeService.deleteById(id);

        if (!result) {
            var errorMessage = new Message("error", "Failed to delete recipe");
            redirectAttributes.addFlashAttribute("message", errorMessage);
        } else {
            var successMessage = new Message("success", "Recipe deleted successfully");
            redirectAttributes.addFlashAttribute("message", successMessage);
        }
        return "redirect:/manage/recipes";
    }
}
