package com.congdinh.recipeapp.dtos.category;

import com.congdinh.recipeapp.dtos.core.SearchDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySearchDTO extends SearchDTO {
    private String keyword;
}
