package com.smartparking.management.controller;

import com.smartparking.management.dto.request.RegisterRequest;
import com.smartparking.management.dto.request.UpdateProfileRequest;
import com.smartparking.management.dto.response.UserResponse;
import com.smartparking.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsersIncludingDeleted() {
        return ResponseEntity.ok(userService.getAllUsersIncludingDeleted());
    }

    @GetMapping("/search/email")
    public ResponseEntity<List<UserResponse>> searchByEmail(@RequestParam String email) {
        List<UserResponse>  response = userService.searchByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/phone")
    public ResponseEntity<List<UserResponse>> searchByPhoneNumber(@RequestParam String phoneNumber) {
        List<UserResponse>  responses = userService.searchByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateUser(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<UserResponse> restoreUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.restoreUser(id));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

}
