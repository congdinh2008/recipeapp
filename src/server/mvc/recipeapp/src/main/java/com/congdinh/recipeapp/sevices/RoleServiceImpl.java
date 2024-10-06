package com.congdinh.recipeapp.sevices;

import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.recipeapp.dtos.auth.RoleCreateDTO;
import com.congdinh.recipeapp.dtos.auth.RoleDTO;
import com.congdinh.recipeapp.entities.Role;
import com.congdinh.recipeapp.repositories.RoleRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleDTO> findAll() {
        // Get List of entities
        var roles = roleRepository.findAll();

        // Convert entities to DTOs
        var roleDTOs = roles.stream().map(role -> {
            var roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
            return roleDTO;
        }).toList();

        // Return DTOs
        return roleDTOs;
    }

     @Override
    public Page<RoleDTO> findAll(String keyword, Pageable pageable) {
         // Find role by keyword
         Specification<Role> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(name) LIKE %keyword%
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%");

            Predicate desPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(namePredicate, desPredicate);
        };

        var roles = roleRepository.findAll(specification, pageable);

        // Covert Page<Role> to Page<RoleDTO>
        var roleDTOs = roles.map(role -> {
            var roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
            return roleDTO;
        });

        return roleDTOs;
    }

    @Override
    public RoleDTO findById(UUID id) {
        // Get entity by id
        var role = roleRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (role == null) {
            return null;
        }

        // Convert entity to DTO
        var roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());

        // Return DTO
        return roleDTO;
    }

    @Override
    public RoleDTO create(RoleCreateDTO roleCreateDTO) {
        // Check if roleDTO is null then throw exception
        if (roleCreateDTO == null) {
            throw new IllegalArgumentException("Role is required");
        }

        // Check if role with the same name exists
        var role = roleRepository.findByName(roleCreateDTO.getName());

        if (role != null) {
            throw new IllegalArgumentException("Role with the same name already exists");
        }

        // Convert DTO to entity
        role = new Role();
        role.setName(roleCreateDTO.getName());
        role.setDescription(roleCreateDTO.getDescription());

        // Save entity
        role = roleRepository.save(role);

        // Convert entity to DTO
        var roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());

        // Return DTO
        return roleDTO;
    }

    @Override
    public RoleDTO update(UUID id, RoleDTO roleDTO) {
        // Check if roleDTO is null then throw exception
        if (roleDTO == null) {
            throw new IllegalArgumentException("Role is required");
        }

        // Get entity by id
        var role = roleRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (role == null) {
            return null;
        }

        // Check if role with the same name exists
        var roleWithSameName = roleRepository.findByName(roleDTO.getName());

        if (roleWithSameName != null && !roleWithSameName.getId().equals(id)) {
            throw new IllegalArgumentException("Role with the same name already exists");
        }

        // Update entity
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());

        // Save entity
        role = roleRepository.save(role);

        // Convert entity to DTO
        // Only id is needed because name and description are already in roleDTO
        roleDTO.setId(role.getId());

        // Return DTO
        return roleDTO;
    }

    @Override
    public boolean deleteById(UUID id) {
        // Check if entity exists
        var role = roleRepository.findById(id).orElse(null);

        // Check if entity is null then return
        if (role == null) {
            return false;
        }

        // Delete entity
        roleRepository.delete(role);

        // Check if entity is deleted
        var isDeleted = roleRepository.findById(id).isEmpty();

        // Return if entity is deleted
        return isDeleted;
    }
}
