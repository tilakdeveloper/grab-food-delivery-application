package com.grab.FoodApp.auth_users.controllers;

import com.grab.FoodApp.auth_users.dtos.UserDTO;
import com.grab.FoodApp.auth_users.services.UserService;
import com.grab.FoodApp.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers() {
        Response<List<UserDTO>> response = userService.getAllUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping(value="/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<?>> updateOwnAccount(@ModelAttribute @Valid UserDTO userDTO,
                                                        @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        userDTO.setImageFile(imageFile);
        Response<?> response = userService.updateOwnAccount(userDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<Response<?>> deactivateOwnAccount() {
        Response<?> response = userService.deactivateOwnAccount();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/account" )
    public ResponseEntity<Response<UserDTO>> getOwnAccountDetails() {
        Response<UserDTO> response = userService.getOwnAccountDetails();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
