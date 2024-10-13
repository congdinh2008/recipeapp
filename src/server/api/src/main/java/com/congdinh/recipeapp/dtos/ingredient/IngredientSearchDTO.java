package com.congdinh.recipeapp.dtos.ingredient;

import com.congdinh.recipeapp.dtos.core.SearchDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSearchDTO extends SearchDTO {
    private String keyword;
}
