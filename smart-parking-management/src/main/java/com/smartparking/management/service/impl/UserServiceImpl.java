package com.smartparking.management.service.impl;

import com.smartparking.management.dto.request.RegisterRequest;
import com.smartparking.management.dto.request.UpdateProfileRequest;
import com.smartparking.management.dto.response.UserResponse;
import com.smartparking.management.entity.User;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.UserMapper;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserResponse getUserById(Long id) {
        User user = getActiveUser(id);
        return UserMapper.mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByDeletedFalse()
                .stream()
                .map(UserMapper::mapToResponse)
                .toList();
    }

    @Override
    public List<UserResponse> searchByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCaseAndDeletedFalse(email)
                .stream()
                .map(UserMapper::mapToResponse)
                .toList();
    }

    @Override
    public List<UserResponse> searchByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumberContainingAndDeletedFalse(phoneNumber)
                .stream()
                .map(UserMapper::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(UpdateProfileRequest request) {
        User user = getLoggedInUser();
        validatePhoneNumber(user, request.getPhoneNumber());
        UserMapper.updateEntity(user, request);
        User saved = userRepository.save(user);
        return UserMapper.mapToResponse(saved);
    }

    @Override
    public void deleteUser(Long id) {

        User user = getActiveUser(id);
        if (user.getDeleted()) {
            throw new BadRequestException("User is already deleted");
        }
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public UserResponse restoreUser(Long id) {
        User user = getUser(id);

        if (!user.getDeleted()) {
            throw new BadRequestException("User is already active");
        }
        user.setDeleted(false);
        User saved = userRepository.save(user);
        return UserMapper.mapToResponse(saved);
    }

    private User getActiveUser(Long id){
        return userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id : " + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));
    }

    private void validatePhoneNumber(User user, String phoneNumber){
        if (!user.getPhoneNumber().equals(phoneNumber) &&
         userRepository.existsByPhoneNumberAndDeletedFalse(phoneNumber)){
            throw new BadRequestException("Phone Number already exists");
        }
    }

    private User getLoggedInUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));
    }

    @Override
    public UserResponse getProfile() {
        return UserMapper.mapToResponse(getLoggedInUser());
    }

}
