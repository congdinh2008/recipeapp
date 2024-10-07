package com.congdinh.recipeapp.entities;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(100)")
    private String name;

    @OneToMany(mappedBy = "ingredient")
    private Set<RecipeIngredient> recipes = new HashSet<>();
}
