package com.congdinh.recipeapp.dtos.ingredient;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {
    private UUID id;

    @NotBlank(message = "Name is required")
    @Length(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
}
