package com.smartparking.management.service.impl;

import com.smartparking.management.dto.response.BookingReportResponse;
import com.smartparking.management.dto.response.PaymentReportResponse;
import com.smartparking.management.dto.response.RevenueReportResponse;
import com.smartparking.management.entity.Booking;
import com.smartparking.management.entity.Payment;
import com.smartparking.management.enums.PaymentStatus;
import com.smartparking.management.repository.BookingRepository;
import com.smartparking.management.repository.PaymentRepository;
import com.smartparking.management.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;


    @Override
    public RevenueReportResponse getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        RevenueReportResponse response = new RevenueReportResponse();

        response.setFromDate(fromDate);
        response.setToDate(toDate);
        response.setTotalRevenue(paymentRepository.getRevenueBetween(start, end));
        response.setTotalPayments(paymentRepository.countByPaymentStatusAndPaymentTimeBetween(PaymentStatus.PAID, start, end));

        return response;
    }

    @Override
    public List<BookingReportResponse> getBookingReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return bookingRepository.findByBookingTimeBetween(start, end)
                .stream()
                .map(this::mapToBookingReport)
                .toList();
    }

    @Override
    public List<PaymentReportResponse> getPaymentReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return paymentRepository.findByPaymentTimeBetween(start, end)
                .stream()
                .map(this::mapToPaymentReport)
                .toList();
    }










    private BookingReportResponse mapToBookingReport(Booking booking) {

        BookingReportResponse response = new BookingReportResponse();

        response.setBookingNumber(booking.getBookingNumber());
        response.setUserName(booking.getUser().getName());
        response.setVehicleNumber(booking.getVehicle().getVehicleNumber());
        response.setVehicleType(booking.getVehicle().getVehicleType());
        response.setAreaName(booking.getParkingArea().getAreaName());
        response.setSlotNumber(booking.getParkingSlot().getSlotNumber());
        response.setBookingStatus(booking.getBookingStatus());
        response.setBookingTime(booking.getBookingTime());

        return response;
    }

    private PaymentReportResponse mapToPaymentReport(Payment payment) {

        PaymentReportResponse response = new PaymentReportResponse();

        response.setPaymentNumber(payment.getPaymentNumber());
        response.setTransactionId(payment.getTransactionId());
        response.setBookingNumber(payment.getBooking().getBookingNumber());
        response.setUserName(payment.getBooking().getUser().getName());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setPaymentTime(payment.getPaymentTime());

        return response;
    }
}
