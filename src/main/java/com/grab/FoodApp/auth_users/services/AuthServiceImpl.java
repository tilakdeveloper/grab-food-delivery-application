package com.grab.FoodApp.auth_users.services;

import com.grab.FoodApp.auth_users.dtos.LoginRequest;
import com.grab.FoodApp.auth_users.dtos.LoginResponse;
import com.grab.FoodApp.auth_users.dtos.RegistrationRequest;
import com.grab.FoodApp.auth_users.dtos.UserDTO;
import com.grab.FoodApp.auth_users.entity.User;
import com.grab.FoodApp.auth_users.repository.UserRepository;
import com.grab.FoodApp.exceptions.BadRequestException;
import com.grab.FoodApp.exceptions.NotFoundException;
import com.grab.FoodApp.response.Response;
import com.grab.FoodApp.role.entity.Role;
import com.grab.FoodApp.role.repository.RoleRepository;
import com.grab.FoodApp.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response<?> register(RegistrationRequest registrationRequest) {
        log.info("Registering user with email: {}", registrationRequest.getEmail());

        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            return Response.builder()
                    .message("User with this email already exists: " + registrationRequest.getEmail())
                    .statusCode(400)
                    .build();
        }

        // Assign roles to the user
        List<Role> userRoles;
        if (registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty()) {
            userRoles = registrationRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName.toUpperCase())
                            .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                    .toList();
        } else {
            Role defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("Default role CUSTOMER not found"));
            userRoles = List.of(defaultRole);
        }

        User userToSave = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .address(registrationRequest.getAddress())
                .roles(userRoles)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(userToSave);

        log.info("User registered successfully with id: {}", savedUser.getId());

        return Response.<UserDTO>builder()
                .data(modelMapper.map(savedUser, UserDTO.class))
                .message("User registered successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        log.info("User attempting to login with email: {}", loginRequest.getEmail());

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + loginRequest.getEmail()));

        if (!user.isActive()) {
            throw new NotFoundException("Account is inactive. Please contact customer support.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(user.getEmail());

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(roleNames)
                .token(token)
                .build();

        log.info("User logged in successfully with id: {}", user.getId());

        return Response.<LoginResponse>builder()
                .data(loginResponse)
                .message("Login successful")
                .statusCode(HttpStatus.OK.value())
                .build();
    }
}
