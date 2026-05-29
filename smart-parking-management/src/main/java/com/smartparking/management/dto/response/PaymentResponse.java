package com.smartparking.management.dto.response;

import com.smartparking.management.enums.PaymentMethod;
import com.smartparking.management.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;
    private String paymentNumber;
    private String transactionId;
    private String bookingNumber;

    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentTime;
    private String vehicleNumber;
    private String slotNumber;
    private String areaName;

    private String qrCodeBase64;

}
