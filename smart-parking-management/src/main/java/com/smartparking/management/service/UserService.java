package com.smartparking.management.service;

import com.smartparking.management.dto.request.UpdateProfileRequest;
import com.smartparking.management.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    List<UserResponse> searchByEmail(String email);
    List<UserResponse> searchByPhoneNumber(String phoneNumber);
    UserResponse updateUser(UpdateProfileRequest request);
    void deleteUser(Long id);
    UserResponse restoreUser(Long id);

    UserResponse getProfile();

    List<UserResponse> getAllUsersIncludingDeleted();
}
