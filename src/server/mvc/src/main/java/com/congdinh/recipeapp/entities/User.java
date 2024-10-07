package com.congdinh.recipeapp.entities;

import java.util.UUID;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String firstName;

    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String lastName;

    // Skip Display Name = First Name + Last Name
    @Transient
    private String displayName = firstName + " " + lastName;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
}
