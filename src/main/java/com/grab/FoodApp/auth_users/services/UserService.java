package com.grab.FoodApp.auth_users.services;

import com.grab.FoodApp.auth_users.dtos.UserDTO;
import com.grab.FoodApp.auth_users.entity.User;
import com.grab.FoodApp.response.Response;

import java.util.List;

public interface UserService {

     User getCurrentLoggedInUser();

     Response<List<UserDTO>> getAllUsers();

     Response<UserDTO> getOwnAccountDetails();

     Response<?> updateOwnAccount(UserDTO userDTO);

     Response<?> deactivateOwnAccount();
}
