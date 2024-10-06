package com.congdinh.recipeapp.sevices;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.congdinh.recipeapp.dtos.auth.RoleCreateDTO;
import com.congdinh.recipeapp.dtos.auth.RoleDTO;

public interface RoleService {
    List<RoleDTO> findAll();

    Page<RoleDTO> findAll(String keyword, Pageable pageable);

    RoleDTO findById(UUID id);

    RoleDTO create(RoleCreateDTO roleCreateDTO);

    RoleDTO update(UUID id, RoleDTO roleDTO);

    boolean deleteById(UUID id);
}
