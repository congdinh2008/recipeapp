package com.congdinh.recipeapp.dtos.auth;

import com.congdinh.recipeapp.dtos.core.SearchDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDTO extends SearchDTO {
    private String keyword;
}
