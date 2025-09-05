package com.grab.FoodApp.auth_users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    private String phoneNumber;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;

    @NotBlank(message = "Address is mandatory")
    private String address;

    private List<String> roles;
}
