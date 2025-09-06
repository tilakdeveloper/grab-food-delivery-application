package com.grab.FoodApp.auth_users.services;

import com.grab.FoodApp.auth_users.dtos.UserDTO;
import com.grab.FoodApp.auth_users.entity.User;
import com.grab.FoodApp.auth_users.repository.UserRepository;
import com.grab.FoodApp.cloudinary.CloudinaryService;
import com.grab.FoodApp.email_notification.dtos.NotificationDTO;
import com.grab.FoodApp.email_notification.services.NotificationService;
import com.grab.FoodApp.exceptions.BadRequestException;
import com.grab.FoodApp.exceptions.NotFoundException;
import com.grab.FoodApp.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final CloudinaryService cloudinaryService;

    @Override
    public User getCurrentLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return Response.<List<UserDTO>>builder()
                .data(userDTOs)
                .message("Users retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<UserDTO> getOwnAccountDetails() {
        User user = getCurrentLoggedInUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return Response.<UserDTO>builder()
                .data(userDTO)
                .message("User details retrieved successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @SneakyThrows
    @Override
    public Response<?> updateOwnAccount(UserDTO userDTO) {
        User existingUser = getCurrentLoggedInUser();

        String profilePublicId = existingUser.getProfilePublicId();

        MultipartFile imageFile = userDTO.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            if (profilePublicId != null && !profilePublicId.isEmpty()) {
                cloudinaryService.deleteFile(profilePublicId);
            }

            //Upload new image to cloud storage and get the URL and public ID
            Map<String, String> uploadFileDetails = cloudinaryService.uploadFile(imageFile); // Replace with actual upload
            existingUser.setProfileUrl(uploadFileDetails.get("url"));
            existingUser.setProfilePublicId(uploadFileDetails.get("public_id"));
        }

        if (userDTO.getName() != null) {
            existingUser.setName(userDTO.getName());
        }
        if (userDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getAddress() != null) {
            existingUser.setAddress(userDTO.getAddress());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new BadRequestException("Email is already in use: " + userDTO.getEmail());
            }
            String oldEmail = existingUser.getEmail();
            existingUser.setEmail(userDTO.getEmail());
        }

        userRepository.save(existingUser);

        return Response.builder()
                .message("User account updated successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<?> deactivateOwnAccount() {
        User existingUser = getCurrentLoggedInUser();
        existingUser.setActive(false);
        userRepository.save(existingUser);

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(existingUser.getEmail())
                .subject("Account Deactivation")
                .body("Your account has been deactivated. If this was not you, please contact support.")
                .build();

        notificationService.sendEmail(notificationDTO);

        return Response.builder()
                .message("User account deactivated successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }
}
