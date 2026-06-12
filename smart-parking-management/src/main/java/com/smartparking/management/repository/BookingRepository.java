package com.smartparking.management.repository;

import com.smartparking.management.entity.Booking;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    Optional<Booking> findByBookingNumber(String bookingNumber);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByBookingStatus(BookingStatus bookingStatus);

    List<Booking> findByUserIdAndBookingStatus(Long userId, BookingStatus bookingStatus);
    List<Booking> findByUserIdAndVehicleVehicleType(Long userId, VehicleType vehicleType);

    List<Booking> findByUserIdAndBookingTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    List<Booking> findByVehicleVehicleType(VehicleType vehicleType);
    List<Booking> findByBookingTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookingStatusAndPaymentExpiryTimeBefore(
            BookingStatus bookingStatus, LocalDateTime time);

    long count();

    long countByBookingStatus(BookingStatus bookingStatus);
    long countByUserId(Long userId);
    long countByUserIdAndBookingStatus(Long userId, BookingStatus bookingStatus);

    long countByParkingAreaOwnerId(Long ownerId);
    long countByParkingAreaOwnerIdAndBookingStatus(Long ownerId, BookingStatus bookingStatus);

    List<Booking> findByBookingStatusAndExpectedExitTimeBefore(BookingStatus bookingStatus, LocalDateTime dateTime);
}
