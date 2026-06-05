package com.smartparking.management.service.impl;

import com.smartparking.management.config.JwtFilter;
import com.smartparking.management.config.JwtUtil;
import com.smartparking.management.dto.request.*;
import com.smartparking.management.dto.response.AuthResponse;
import com.smartparking.management.dto.response.UserResponse;
import com.smartparking.management.entity.User;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.exceptions.UnauthorizedException;
import com.smartparking.management.mapper.UserMapper;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.AuthService;
import com.smartparking.management.service.EmailService;
import com.smartparking.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private final Map<String, PendingUserData> pendingUsers = new ConcurrentHashMap<>();
    private final Map<String, OtpData> resetOtpStore = new ConcurrentHashMap<>();


    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByPhoneNumberAndDeletedFalse(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already exists");
        }

        User user = UserMapper.mapToEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        String otp = generateOtp();
        pendingUsers.put(request.getEmail(),
                new PendingUserData(user, otp, LocalDateTime.now().plusMinutes(5)));

        emailService.sendRegistrationOtp(request.getEmail(), otp);
        return UserMapper.mapToResponse(user);
    }

    @Override
    public UserResponse verifyOtp(VerifyOtpRequest request) {

        PendingUserData pendingUserData = pendingUsers.get(request.getEmail());

        if (pendingUserData == null) {
            throw new BadRequestException("No registration request found for this email");
        }
        if (pendingUserData.expiryTime().isBefore(LocalDateTime.now())) {
            pendingUsers.remove(request.getEmail());
            throw new BadRequestException("OTP expired");
        }
        if (!pendingUserData.otp().equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        User savedUser = userRepository.save(pendingUserData.user());
        pendingUsers.remove(request.getEmail());
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());
        return UserMapper.mapToResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(), user.getRole().name());


        return new AuthResponse(token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                "Login successful"
        );
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String otp = generateOtp();

        resetOtpStore.put(user.getEmail(),
                new OtpData(otp, LocalDateTime.now().plusMinutes(5)));
        emailService.sendPasswordResetOtp(user.getEmail(), otp);
        return "Password reset OTP sent successfully";
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }
        OtpData otpData = resetOtpStore.get(request.getEmail());
        if (otpData == null) {
            throw new BadRequestException("OTP not found");
        }

        if (otpData.expiryTime().isBefore(LocalDateTime.now())) {
            resetOtpStore.remove(request.getEmail());
            throw new BadRequestException("OTP expired");
        }
        if (!otpData.otp().equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        User user = userRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetOtpStore.remove(request.getEmail());
        return "Password reset successfully";
    }

    private String generateOtp(){
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000)+100000;
        return String.valueOf(otp);
    }

    private record PendingUserData(User user, String otp, LocalDateTime expiryTime){
    }
    private record OtpData(String otp,LocalDateTime expiryTime){
    }
}
