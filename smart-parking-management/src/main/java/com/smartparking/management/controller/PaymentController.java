package com.smartparking.management.controller;

import com.smartparking.management.dto.request.PaymentRequest;
import com.smartparking.management.dto.response.PaymentResponse;
import com.smartparking.management.enums.PaymentStatus;
import com.smartparking.management.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.makePayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/failed/{bookingId}")
    public ResponseEntity<PaymentResponse> markPaymentFailed(@PathVariable Long bookingId) {
        PaymentResponse response = paymentService.markPaymentFailed(bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/search")
    public ResponseEntity<PaymentResponse> getPaymentByNumber(@RequestParam String paymentNumber) {
        return ResponseEntity.ok(paymentService.getPaymentByNumber(paymentNumber));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentByBookingId(bookingId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments() {
        return ResponseEntity.ok(paymentService.getMyPayments());
    }

    @GetMapping("/my/date-range")
    public ResponseEntity<List<PaymentResponse>> getMyPaymentsByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return ResponseEntity.ok(paymentService.getMyPaymentsByDateRange(fromDate, toDate));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/status")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(
            @RequestParam PaymentStatus paymentStatus) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(paymentStatus));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return ResponseEntity.ok(paymentService.getPaymentsByDateRange(fromDate, toDate));
    }



}
