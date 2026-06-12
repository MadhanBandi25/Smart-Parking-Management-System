package com.smartparking.management.service;

import com.smartparking.management.dto.request.CreateBookingRequest;
import com.smartparking.management.dto.response.BookingResponse;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.VehicleType;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingResponse createBooking(CreateBookingRequest request);
    BookingResponse getBookingById(Long bookingId);
    BookingResponse getBookingByNumber(String bookingNumber);
    List<BookingResponse> getAllBookings();

    List<BookingResponse> getMyBookings();
    List<BookingResponse> getMyBookingsByStatus(BookingStatus bookingStatus);
    List<BookingResponse> getMyBookingsByVehicleType(VehicleType vehicleType);
    List<BookingResponse> getMyBookingsByDateRange(LocalDate fromDate, LocalDate toDate);

    List<BookingResponse> getBookingsByStatus(BookingStatus bookingStatus);
    List<BookingResponse> getBookingsByVehicleType(VehicleType vehicleType);
    List<BookingResponse> getBookingsByDateRange(LocalDate fromDate, LocalDate toDate);

    BookingResponse startBooking(Long bookingId);
    BookingResponse completeBooking(Long bookingId);
    BookingResponse cancelBooking(Long bookingId);

}
