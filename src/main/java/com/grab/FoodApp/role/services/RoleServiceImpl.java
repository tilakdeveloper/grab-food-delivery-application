package com.grab.FoodApp.role.services;

import com.grab.FoodApp.exceptions.BadRequestException;
import com.grab.FoodApp.exceptions.NotFoundException;
import com.grab.FoodApp.response.Response;
import com.grab.FoodApp.role.dtos.RoleDTO;
import com.grab.FoodApp.role.entity.Role;
import com.grab.FoodApp.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response<RoleDTO> createRole(RoleDTO roleDTO) {
        Role role = modelMapper.map(roleDTO, Role.class);
        Role savedRole = roleRepository.save(role);

        return Response.<RoleDTO>builder()
                .data(modelMapper.map(savedRole, RoleDTO.class))
                .message("Role created successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<RoleDTO> updateRole(RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(roleDTO.getId())
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleDTO.getId()));

        if(roleRepository.findByName(roleDTO.getName()).isPresent()) {
            throw new BadRequestException("Role with this name already exists: " + roleDTO.getName());
        }

        existingRole.setName(roleDTO.getName());
        Role updatedRole = roleRepository.save(existingRole);

        return Response.<RoleDTO>builder()
                .data(modelMapper.map(updatedRole, RoleDTO.class))
                .message("Role updated successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<List<RoleDTO>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> roleDTOs = roles.stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .toList();

        return Response.<List<RoleDTO>>builder()
                .data(roleDTOs)
                .message("Roles retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));

        roleRepository.delete(existingRole);

        return Response.builder()
                .message("Role deleted successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }
}

