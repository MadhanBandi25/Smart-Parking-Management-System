package com.smartparking.management.service;

import com.smartparking.management.dto.request.PaymentRequest;
import com.smartparking.management.dto.response.PaymentResponse;
import com.smartparking.management.enums.PaymentMethod;
import com.smartparking.management.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {

    PaymentResponse makePayment(PaymentRequest request);
    PaymentResponse markPaymentFailed(Long bookingId);

    PaymentResponse getPaymentById(Long paymentId);
    PaymentResponse getPaymentByNumber(String paymentNumber);
    PaymentResponse getPaymentByBookingId(Long bookingId);

    List<PaymentResponse> getMyPayments();
    List<PaymentResponse> getMyPaymentsByDateRange(LocalDate fromDate, LocalDate toDate);

    List<PaymentResponse> getAllPayments();
    List<PaymentResponse> getPaymentsByStatus(PaymentStatus paymentStatus);
    List<PaymentResponse> getPaymentsByDateRange(LocalDate fromDate, LocalDate toDate);

    PaymentResponse makeExtraPayment(Long bookingId, PaymentMethod paymentMethod);
}
