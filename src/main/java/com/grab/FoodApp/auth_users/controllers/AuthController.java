package com.grab.FoodApp.auth_users.controllers;

import com.grab.FoodApp.auth_users.dtos.LoginRequest;
import com.grab.FoodApp.auth_users.dtos.LoginResponse;
import com.grab.FoodApp.auth_users.dtos.RegistrationRequest;
import com.grab.FoodApp.auth_users.services.AuthService;
import com.grab.FoodApp.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<?>> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
         Response<?> response = authService.register(registrationRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        Response<LoginResponse> response = authService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
