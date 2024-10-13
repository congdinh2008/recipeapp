package com.congdinh.recipeapp.dtos.recipe;

import com.congdinh.recipeapp.dtos.core.SearchDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSearchDTO extends SearchDTO {
    private String keyword;
}
