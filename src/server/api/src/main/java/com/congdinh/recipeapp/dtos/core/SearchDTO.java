package com.congdinh.recipeapp.dtos.core;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    private int page;
    private int size;
    private String sort;
    private SortDirection direction;
}
