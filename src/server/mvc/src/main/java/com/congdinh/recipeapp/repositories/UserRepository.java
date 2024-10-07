package com.congdinh.recipeapp.repositories;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.congdinh.recipeapp.entities.User;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);

    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findById(UUID id);
}
