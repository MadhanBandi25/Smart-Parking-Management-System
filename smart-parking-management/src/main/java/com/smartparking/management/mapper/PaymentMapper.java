package com.smartparking.management.mapper;

import com.smartparking.management.dto.response.PaymentResponse;
import com.smartparking.management.entity.Payment;

public class PaymentMapper {

    public static PaymentResponse mapToPaymentResponse(Payment payment) {

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());

        response.setPaymentNumber(payment.getPaymentNumber());
        response.setTransactionId(payment.getTransactionId());

        response.setBookingNumber(payment.getBooking().getBookingNumber());

        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setPaymentTime(payment.getPaymentTime());

        response.setVehicleNumber(payment.getBooking().getVehicle().getVehicleNumber());
        response.setSlotNumber(payment.getBooking().getParkingSlot().getSlotNumber());
        response.setAreaName(payment.getBooking().getParkingArea().getAreaName());

        return response;
    }

}
