package com.smartparking.management.controller;

import com.smartparking.management.dto.response.BookingResponse;
import com.smartparking.management.dto.response.QrCodeResponse;
import com.smartparking.management.service.BookingService;
import com.smartparking.management.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/booking/{bookingNumber}")
    public ResponseEntity<QrCodeResponse> generateQrCode(
            @PathVariable String bookingNumber) {
        return ResponseEntity.ok(qrCodeService.generateQrCode(bookingNumber));
    }

    @GetMapping("/verify")
    public ResponseEntity<BookingResponse> verifyQrCode(
            @RequestParam String bookingNumber) {
        return ResponseEntity.ok(qrCodeService.verifyQrCode(bookingNumber));
    }

    @PutMapping("/check-in/{bookingId}")
    public ResponseEntity<BookingResponse> checkIn(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.startBooking(bookingId));
    }

    @PutMapping("/check-out/{bookingId}")
    public ResponseEntity<BookingResponse> checkOut(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.completeBooking(bookingId));
    }

}
