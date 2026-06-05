package com.smartparking.management.mapper;

import com.smartparking.management.dto.request.RegisterRequest;
import com.smartparking.management.dto.request.UpdateProfileRequest;
import com.smartparking.management.dto.response.UserResponse;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.Role;

public class UserMapper {

    public static User mapToEntity(RegisterRequest request) {

        User user = new User();

        user.setName(formatName(request.getName()));
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole()!= null ? request.getRole() : Role.USER);
        return user;
    }

    public static UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public static void updateEntity(User user, UpdateProfileRequest request) {
        user.setName(formatName(request.getName()));
        user.setPhoneNumber(request.getPhoneNumber());
    }

    private static String formatName(String name) {
        String[] words = name.trim().toLowerCase().split("\\s+");

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

}
