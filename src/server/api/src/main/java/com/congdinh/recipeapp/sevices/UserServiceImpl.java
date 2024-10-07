package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.recipeapp.dtos.auth.RoleDTO;
import com.congdinh.recipeapp.dtos.auth.UserCreateDTO;
import com.congdinh.recipeapp.dtos.auth.UserDTO;
import com.congdinh.recipeapp.entities.Role;
import com.congdinh.recipeapp.entities.User;
import com.congdinh.recipeapp.repositories.RoleRepository;
import com.congdinh.recipeapp.repositories.UserRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDTO> findAll() {
        // Get List of entities
        var users = userRepository.findAll();

        // Convert entities to DTOs
        var userDTOs = users.stream().map(user -> {
            var userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setDisplayName(user.getDisplayName());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());

            // Convert roles to role
            List<RoleDTO> roleDTOs = user.getRoles().stream().map(role -> {
                var roleDTO = new RoleDTO();
                roleDTO.setId(role.getId());
                roleDTO.setName(role.getName());
                roleDTO.setDescription(role.getDescription());
                return roleDTO;
            }).toList();

            userDTO.setRoles(roleDTOs);
            userDTO.setRoleIds(roleDTOs.stream().map(RoleDTO::getId).collect(Collectors.toList()));

            return userDTO;
        }).toList();

        // Return DTOs
        return userDTOs;
    }

    @Override
    public Page<UserDTO> findAll(String keyword, Pageable pageable) {
        // Find user by keyword
        Specification<User> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // WHERE LOWER(firstName) LIKE %keyword%
            Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(lastName) LIKE %keyword%
            Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(userName) LIKE %keyword%
            Predicate userNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(email) LIKE %keyword%
            Predicate emailPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword% OR
            // LOWER(username) LIKE %keyword% OR LOWER(email) LIKE %keyword%
            return criteriaBuilder.or(firstNamePredicate, lastNamePredicate, userNamePredicate, emailPredicate);
        };

        var users = userRepository.findAll(specification, pageable);

        // Covert Page<User> to Page<UserDTO>
        var userDTOs = users.map(user -> {
            var userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setDisplayName(user.getDisplayName());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());

            // Convert roles to roles
            List<RoleDTO> roleDTOs = user.getRoles().stream().map(role -> {
                var roleDTO = new RoleDTO();
                roleDTO.setId(role.getId());
                roleDTO.setName(role.getName());
                roleDTO.setDescription(role.getDescription());
                return roleDTO;
            }).toList();

            userDTO.setRoles(roleDTOs);
            userDTO.setRoleIds(roleDTOs.stream().map(RoleDTO::getId).collect(Collectors.toList()));

            return userDTO;
        });

        return userDTOs;
    }

    @Override
    public UserDTO findById(UUID id) {
        // Get entity by id
        var user = userRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (user == null) {
            return null;
        }

        // Convert entity to DTO
        var userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        // Convert roles to roles
        List<RoleDTO> roleDTOs = user.getRoles().stream().map(role -> {
            var roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            roleDTO.setDescription(role.getDescription());
            return roleDTO;
        }).toList();

        userDTO.setRoles(roleDTOs);
        userDTO.setRoleIds(roleDTOs.stream().map(RoleDTO::getId).collect(Collectors.toList()));

        // Return DTO
        return userDTO;
    }

    @Override
    public UserDTO create(UserCreateDTO userCreateDTO) {
        // Check if userDTO is null then throw exception
        if (userCreateDTO == null) {
            throw new IllegalArgumentException("User is required");
        }

        // Check if user with the same username exists
        var user = userRepository.findByUsername(userCreateDTO.getUsername());

        if (user != null) {
            throw new IllegalArgumentException("User with the same username already exists");
        }

        // Convert DTO to entity
        user = new User();
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setEmail(userCreateDTO.getEmail());

        if (userCreateDTO.getRoleIds() != null && !userCreateDTO.getRoleIds().isEmpty()) {
            var existingRoles = roleRepository.findAll();

            List<Role> roles = userCreateDTO.getRoleIds().stream().map(roleId -> {
                var existedRole = existingRoles.stream().filter(role -> role.getId().equals(roleId)).findFirst()
                        .orElse(null);

                if (existedRole == null) {
                    throw new IllegalArgumentException("Role with id " + roleId + " does not exist");
                }

                return existedRole;
            }).collect(Collectors.toList());

            user.setRoles(roles);
        }

        // Save entity
        user = userRepository.save(user);

        // Convert entity to DTO
        var userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setUsername(user.getUsername());

        // Return DTO
        return userDTO;
    }

    @Override
    public UserDTO update(UUID id, UserDTO userDTO) {
        // Check if userDTO is null then throw exception
        if (userDTO == null) {
            throw new IllegalArgumentException("User is required");
        }

        // Get entity by id
        var user = userRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (user == null) {
            return null;
        }

        // Check if user with the same username exists
        var userWithSameUsername = userRepository.findByUsername(userDTO.getUsername());

        if (userWithSameUsername != null && !userWithSameUsername.getId().equals(id)) {
            throw new IllegalArgumentException("User with the same username already exists");
        }

        // Update entity
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        // Set Role to entity

        if(userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            // Remove all roles
            user.getRoles().clear();

            var existingRoles = roleRepository.findAll();

            List<Role> roles = userDTO.getRoleIds().stream().map(roleId -> {
                var existedRole = existingRoles.stream().filter(role -> role.getId().equals(roleId)).findFirst()
                        .orElse(null);

                if (existedRole == null) {
                    throw new IllegalArgumentException("Role with id " + roleId + " does not exist");
                }

                return existedRole;
            }).collect(Collectors.toList());

            user.setRoles(roles);
        }

        // Save entity
        user = userRepository.save(user);

        // Convert entity to DTO
        // Only id is needed because name and description are already in userDTO
        userDTO.setId(user.getId());

        // Return DTO
        return userDTO;
    }

    @Override
    public UserDTO updatePassword(UUID id, String password) {
        // Check if password is null then throw exception
        if (password == null) {
            throw new IllegalArgumentException("Password is required");
        }

        // Get entity by id
        var user = userRepository.findById(id).orElse(null);

        // Check if entity is null then return null
        if (user == null) {
            return null;
        }

        // Update entity
        user.setPassword(passwordEncoder.encode(password));

        // Save entity
        user = userRepository.save(user);

        // Convert entity to DTO
        var userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setUsername(user.getUsername());

        // Return DTO
        return userDTO;
    }

    @Override
    public boolean deleteById(UUID id) {
        // Check if entity exists
        var user = userRepository.findById(id).orElse(null);

        // Check if entity is null then return
        if (user == null) {
            return false;
        }

        // Delete entity
        userRepository.delete(user);

        // Check if entity is deleted
        var isDeleted = userRepository.findById(id).isEmpty();

        // Return if entity is deleted
        return isDeleted;
    }
}
