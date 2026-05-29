package com.smartparking.management.service.impl;

import com.smartparking.management.exceptions.EmailSendingException;
import com.smartparking.management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendRegistrationOtp(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject("Smart Parking - Email Verification OTP");
            message.setText(
                    "Dear User,\n\n"

                            + "Your OTP for Smart Parking account verification is:\n\n"

                            + "OTP : " + otp + "\n\n"

                            + "This OTP is valid for 5 minutes.\n\n"

                            + "Please do not share this OTP with anyone.\n\n"

                            + "Regards,\n"
                            + "Smart Parking Team"
            );
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send registration OTP email");
        }
    }

    @Override
    public void sendPasswordResetOtp(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject("Smart Parking - Password Reset OTP");
            message.setText(
                    "Dear User,\n\n"

                            + "Your OTP for password reset is:\n\n"

                            + "OTP : " + otp + "\n\n"

                            + "This OTP is valid for 5 minutes.\n\n"

                            + "If you did not request this password reset,"
                            + " please ignore this email.\n\n"

                            + "Regards,\n"
                            + "Smart Parking Team"
            );
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send password reset OTP email");
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject("Welcome to Smart Parking");
            message.setText(
                    "Hello " + name + ",\n\n"
                            + "Welcome to Smart Parking.\n\n"

                            + "Your account has been created successfully.\n\n"

                            + "You can now login, manage vehicles,"
                            + " and book parking slots easily.\n\n"

                            + "Thank you for choosing Smart Parking.\n\n"

                            + "Regards,\n"
                            + "Smart Parking Team"
            );
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send welcome email");
        }
    }

    @Override
    public void sendBookingConfirmedEmail(
            String toEmail,
            String bookingNumber,
            String vehicleNumber,
            String areaName,
            String slotNumber,
            String floorName,
            String entryTime,
            String expectedExitTime) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject("Parking Booking Confirmed");

            message.setText(
                    "Dear User,\n\n"

                            + "Your parking booking has been confirmed successfully.\n\n"

                            + "Booking Details:\n\n"

                            + "Booking Number     : " + bookingNumber + "\n"
                            + "Vehicle Number     : " + vehicleNumber + "\n"
                            + "Parking Area       : " + areaName + "\n"
                            + "Slot Number        : " + slotNumber + "\n"
                            + "Floor              : " + floorName + "\n"
                            + "Entry Time         : " + entryTime + "\n"
                            + "Expected Exit Time : " + expectedExitTime + "\n\n"

                            + "Please use your QR code during entry and exit.\n\n"

                            + "Regards,\n"
                            + "Smart Parking Team"
            );

            javaMailSender.send(message);

        } catch (Exception e) {
            throw new EmailSendingException(
                    "Failed to send booking confirmation email"
            );
        }
    }

    @Override
    public void sendBookingCancelledEmail(
            String toEmail,
            String bookingNumber,
            String vehicleNumber,
            String areaName,
            String slotNumber,
            String floorName) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject("Parking Booking Cancelled");

            message.setText(
                    "Dear User,\n\n"

                            + "Your parking booking has been cancelled successfully.\n\n"

                            + "Booking Details:\n\n"

                            + "Booking Number : " + bookingNumber + "\n"
                            + "Vehicle Number : " + vehicleNumber + "\n"
                            + "Parking Area   : " + areaName + "\n"
                            + "Slot Number    : " + slotNumber + "\n"
                            + "Floor          : " + floorName + "\n\n"

                            + "The slot is now released and available for other users.\n\n"

                            + "Regards,\n"
                            + "Smart Parking Team"
            );

            javaMailSender.send(message);

        } catch (Exception e) {
            throw new EmailSendingException(
                    "Failed to send booking cancellation email"
            );
        }
    }

}
