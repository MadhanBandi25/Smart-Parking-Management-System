package com.smartparking.management.controller;

import com.smartparking.management.dto.request.CreateBookingRequest;
import com.smartparking.management.dto.response.BookingResponse;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.VehicleType;
import com.smartparking.management.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    @GetMapping("/search")
    public ResponseEntity<BookingResponse> getBookingByNumber(@RequestParam String bookingNumber) {
        return ResponseEntity.ok(bookingService.getBookingByNumber(bookingNumber));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/my/status")
    public ResponseEntity<List<BookingResponse>> getMyBookingsByStatus(
            @RequestParam BookingStatus bookingStatus) {
        return ResponseEntity.ok(bookingService.getMyBookingsByStatus(bookingStatus));
    }

    @GetMapping("/my/type")
    public ResponseEntity<List<BookingResponse>> getMyBookingsByVehicleType(
            @RequestParam VehicleType vehicleType) {
        return ResponseEntity.ok(bookingService.getMyBookingsByVehicleType(vehicleType));
    }

    @GetMapping("/my/date-range")
    public ResponseEntity<List<BookingResponse>> getMyBookingsByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {
        return ResponseEntity.ok(bookingService.getMyBookingsByDateRange(fromDate, toDate));
    }

    @GetMapping("/status")
    public ResponseEntity<List<BookingResponse>> getBookingsByStatus(
            @RequestParam BookingStatus bookingStatus) {
        return ResponseEntity.ok(bookingService.getBookingsByStatus(bookingStatus));
    }

    @GetMapping("/type")
    public ResponseEntity<List<BookingResponse>> getBookingsByVehicleType(
            @RequestParam VehicleType vehicleType) {
        return ResponseEntity.ok(bookingService.getBookingsByVehicleType(vehicleType));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<BookingResponse>> getBookingsByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return ResponseEntity.ok(bookingService.getBookingsByDateRange(fromDate, toDate));
    }

    @PutMapping("/{bookingId}/start")
    public ResponseEntity<BookingResponse> startBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.startBooking(bookingId));
    }

    @PutMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResponse> completeBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.completeBooking(bookingId));
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }
}
