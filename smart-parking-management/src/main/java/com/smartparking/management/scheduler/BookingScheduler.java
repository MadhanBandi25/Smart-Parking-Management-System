package com.smartparking.management.scheduler;

import com.smartparking.management.entity.Booking;
import com.smartparking.management.entity.ParkingSlot;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.NotificationType;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.repository.BookingRepository;
import com.smartparking.management.repository.ParkingSlotRepository;
import com.smartparking.management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void cancelExpiredBookings() {

        List<Booking> expiredBookings = bookingRepository
                        .findByBookingStatusAndPaymentExpiryTimeBefore(BookingStatus.PENDING_PAYMENT, LocalDateTime.now());

        for (Booking booking : expiredBookings) {
            booking.setBookingStatus(BookingStatus.CANCELLED);

            Long slotId = booking.getParkingSlot().getId();

            ParkingSlot slot =  parkingSlotRepository.findById(slotId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parking slot not found with id : " + slotId));
            slot.setSlotStatus(SlotStatus.AVAILABLE);

            parkingSlotRepository.save(slot);
            bookingRepository.save(booking);
            notificationService.createNotification(
                    booking.getUser(),
                    "Your booking was auto-cancelled because payment was not completed within 3 minutes.",
                    NotificationType.BOOKING_AUTO_CANCELLED);
        }
    }

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void expireUnusedBookings() {

        List<Booking> expiredBookings = bookingRepository
                .findByBookingStatusAndExpectedExitTimeBefore(BookingStatus.BOOKED, LocalDateTime.now());

        for (Booking booking : expiredBookings) {
            booking.setBookingStatus(BookingStatus.EXPIRED);

            ParkingSlot slot = booking.getParkingSlot();
            slot.setSlotStatus(SlotStatus.AVAILABLE);

            parkingSlotRepository.save(slot);
            bookingRepository.save(booking);

            notificationService.createNotification(
                    booking.getUser(),
                    "Your booking " + booking.getBookingNumber()
                            + " has expired as you did not check in before "
                            + booking.getExpectedExitTime(),
                    NotificationType.BOOKING_AUTO_CANCELLED);
        }
    }
}
