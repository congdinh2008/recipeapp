package com.congdinh.recipeapp.dtos.category;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {
    @NotBlank(message = "Name is required")
    @Length(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Length(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
