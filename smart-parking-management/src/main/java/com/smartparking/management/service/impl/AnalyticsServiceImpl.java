package com.smartparking.management.service.impl;

import com.smartparking.management.dto.response.AnalyticsResponse;
import com.smartparking.management.entity.Payment;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.PaymentStatus;
import com.smartparking.management.repository.BookingRepository;
import com.smartparking.management.repository.PaymentRepository;
import com.smartparking.management.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;


    @Override
    public AnalyticsResponse getAnalytics() {

        AnalyticsResponse response = new AnalyticsResponse();

        response.setTotalRevenue(paymentRepository.getTotalRevenue());
        response.setTodayRevenue(paymentRepository.getTodayRevenue());

        response.setTotalBookings(bookingRepository.count());
        response.setCompletedBookings(
                bookingRepository.countByBookingStatus(BookingStatus.COMPLETED)
        );
        response.setCancelledBookings(
                bookingRepository.countByBookingStatus(BookingStatus.CANCELLED)
        );
        response.setActiveBookings(
                bookingRepository.countByBookingStatus(BookingStatus.ACTIVE)
        );

        var bookings = bookingRepository.findAll();

        Map<String, Long> vehicleTypeBookings = bookings
                .stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getVehicle()
                                .getVehicleType()
                                .name(),
                        Collectors.counting()
                ));

        response.setVehicleTypeBookings(vehicleTypeBookings);

        Map<String, Long> bookingStatusCount = bookings
                .stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getBookingStatus().name(),
                        Collectors.counting()
                ));

        response.setBookingStatusCount(bookingStatusCount);

        Map<String, BigDecimal> areaWiseRevenue = paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentStatus()
                        .equals(PaymentStatus.PAID))
                .collect(Collectors.groupingBy(
                        payment -> payment.getBooking()
                                .getParkingArea()
                                .getAreaName(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Payment::getAmount,
                                BigDecimal::add
                        )
                ));
        response.setAreaWiseRevenue(areaWiseRevenue);
        return response;
    }
}
