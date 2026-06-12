package com.smartparking.management.service;

public interface EmailService {

    void sendRegistrationOtp(String toEmail, String otp);
    void sendPasswordResetOtp(String toEmail, String otp);
    void sendWelcomeEmail(String toEmail, String name);

    void sendBookingConfirmedEmail(
            String toEmail,
            String bookingNumber,
            String vehicleNumber,
            String areaName,
            String slotNumber,
            String floorName,
            String entryTime,
            String expectedExitTime
    );

    void sendBookingCancelledEmail(
            String toEmail,
            String bookingNumber,
            String vehicleNumber,
            String areaName,
            String slotNumber,
            String floorName
    );
}
