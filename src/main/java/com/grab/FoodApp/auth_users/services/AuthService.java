package com.grab.FoodApp.auth_users.services;

import com.grab.FoodApp.auth_users.dtos.LoginRequest;
import com.grab.FoodApp.auth_users.dtos.LoginResponse;
import com.grab.FoodApp.auth_users.dtos.RegistrationRequest;
import com.grab.FoodApp.response.Response;

public interface AuthService {

    Response<?> register(RegistrationRequest registrationRequest);
    Response<LoginResponse> login(LoginRequest loginRequest);
}

