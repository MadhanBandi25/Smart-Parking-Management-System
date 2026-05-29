package com.smartparking.management.service;

import com.smartparking.management.dto.request.*;
import com.smartparking.management.dto.response.AuthResponse;
import com.smartparking.management.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);
    UserResponse verifyOtp(VerifyOtpRequest request);
    AuthResponse login(LoginRequest request);
    String forgotPassword(ForgotPasswordRequest request);
    String resetPassword(ResetPasswordRequest request);
}
