package com.smartparking.management.dto.response;

import com.smartparking.management.enums.PaymentMethod;
import com.smartparking.management.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentReportResponse {

    private String paymentNumber;
    private String transactionId;
    private String bookingNumber;
    private String userName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentTime;
}
