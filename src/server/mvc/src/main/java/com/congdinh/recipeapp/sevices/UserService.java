package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.congdinh.recipeapp.dtos.auth.UserCreateDTO;
import com.congdinh.recipeapp.dtos.auth.UserDTO;

public interface UserService {
    List<UserDTO> findAll();

    Page<UserDTO> findAll(String keyword, Pageable pageable);

    UserDTO findById(UUID id);

    UserDTO create(UserCreateDTO userCreateDTO);

    UserDTO update(UUID id, UserDTO userDTO);

    UserDTO updatePassword(UUID id, String password);

    boolean deleteById(UUID id);
}
