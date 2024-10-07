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

import com.congdinh.recipeapp.dtos.auth.RoleCreateDTO;
import com.congdinh.recipeapp.dtos.auth.RoleDTO;
import com.congdinh.recipeapp.dtos.messages.Message;
import com.congdinh.recipeapp.sevices.RoleService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manage/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
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

        // Search role by keyword and paging
        var roles = roleService.findAll(keyword, pageable);
        model.addAttribute("roles", roles);

        // Passing keyword to view
        model.addAttribute("keyword", keyword);

        // Passing total pages to view
        model.addAttribute("totalPages", roles.getTotalPages());

        // Passing total elements to view
        model.addAttribute("totalElements", roles.getTotalElements());

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
        return "manage/roles/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        var roleCreateDTO = new RoleCreateDTO();
        model.addAttribute("roleCreateDTO", roleCreateDTO);

        return "manage/roles/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute RoleCreateDTO roleCreateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roleCreateDTO", roleCreateDTO);
            return "manage/roles/create";
        }

        var result = roleService.create(roleCreateDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to create role");
            model.addAttribute("message", errorMessage);
            return "manage/roles/create";
        }

        var successMessage = new Message("success", "Role created successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/roles";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable UUID id, Model model) {
        var roleDTO = roleService.findById(id);
        model.addAttribute("roleDTO", roleDTO);

        return "manage/roles/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable UUID id,
            @ModelAttribute @Valid RoleDTO roleDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roleDTO", roleDTO);
            return "manage/roles/edit";
        }

        var result = roleService.update(id, roleDTO);

        if (result == null) {
            var errorMessage = new Message("error", "Failed to update role");
            model.addAttribute("message", errorMessage);
            return "manage/roles/edit";

        }

        var successMessage = new Message("success", "Role updated successfully");
        redirectAttributes.addFlashAttribute("message", successMessage);
        return "redirect:/manage/roles";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        var result = roleService.deleteById(id);

        if (!result) {
            var errorMessage = new Message("error", "Failed to delete role");
            redirectAttributes.addFlashAttribute("message", errorMessage);
        } else {
            var successMessage = new Message("success", "Role deleted successfully");
            redirectAttributes.addFlashAttribute("message", successMessage);
        }
        return "redirect:/manage/roles";
    }
}
