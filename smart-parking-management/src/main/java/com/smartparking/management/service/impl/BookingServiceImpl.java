package com.smartparking.management.service.impl;

import com.smartparking.management.dto.request.CreateBookingRequest;
import com.smartparking.management.dto.response.BookingResponse;
import com.smartparking.management.entity.*;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.NotificationType;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.enums.VehicleType;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.BookingMapper;
import com.smartparking.management.repository.*;
import com.smartparking.management.service.BookingService;
import com.smartparking.management.service.EmailService;
import com.smartparking.management.service.NotificationService;
import com.smartparking.management.util.FloorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private ParkingAreaRepository parkingAreaRepository;
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    @Autowired
    private ParkingRateRepository parkingRateRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;


    @Override
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {

        User user = getLoggedInUser();

        Vehicle vehicle = vehicleRepository.findByIdAndDeletedFalse(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("This vehicle does not belong to logged-in user");
        }

        ParkingArea area = parkingAreaRepository.findByIdAndDeletedFalse(request.getParkingAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking area not found"));

        ParkingSlot slot = parkingSlotRepository.findByIdForUpdate(request.getParkingSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found"));

        if (!slot.getParkingArea().getId().equals(area.getId())) {
            throw new BadRequestException("Selected slot does not belong to selected parking area");
        }
        if (!slot.getVehicleType().equals(vehicle.getVehicleType())) {
            throw new BadRequestException("Vehicle type does not match selected slot type");
        }
        if (!slot.getSlotStatus().equals(SlotStatus.AVAILABLE)) {
            throw new BadRequestException("Parking slot is not available");
        }

        ParkingRate rate = parkingRateRepository
                .findByParkingAreaIdAndVehicleTypeAndDeletedFalse(
                        area.getId(), vehicle.getVehicleType())
                .orElseThrow(() -> new ResourceNotFoundException("Parking rate not found"));

        LocalDateTime now = LocalDateTime.now();

        BigDecimal hourlyRate = getHourlyRate(rate, now);
        BigDecimal baseAmount = hourlyRate.multiply(
                BigDecimal.valueOf(request.getBookedHours())
        );

        Booking booking = new Booking();

        booking.setBookingNumber(null);
        booking.setUser(user);
        booking.setVehicle(vehicle);
        booking.setParkingArea(area);
        booking.setParkingSlot(slot);

        booking.setBookedHours(request.getBookedHours());
        booking.setBookingTime(now);
        booking.setExpectedExitTime(now.plusHours(request.getBookedHours()));
        booking.setActualExitTime(null);

        booking.setExtraMinutes(0L);
        booking.setBaseAmount(baseAmount);
        booking.setExtraAmount(BigDecimal.ZERO);
        booking.setTotalAmount(baseAmount);

        booking.setBookingStatus(BookingStatus.PENDING_PAYMENT);
        booking.setPaymentExpiryTime(LocalDateTime.now().plusMinutes(3));

        slot.setSlotStatus(SlotStatus.RESERVED);
        parkingSlotRepository.save(slot);

        Booking saved = bookingRepository.save(booking);
        notificationService.createNotification(booking.getUser(), "Complete payment within 3 minutes to confirm your booking.", NotificationType.PAYMENT_PENDING);

        return BookingMapper.mapToBookingResponse(saved);
    }

    @Override
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = getBooking(bookingId);
        return BookingMapper.mapToBookingResponse(booking);
    }

    @Override
    public BookingResponse getBookingByNumber(String bookingNumber) {
        Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return BookingMapper.mapToBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getMyBookings() {
        User user = getLoggedInUser();

        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(BookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getMyBookingsByStatus(BookingStatus bookingStatus) {
        User user = getLoggedInUser();

        return bookingRepository
                .findByUserIdAndBookingStatus(user.getId(), bookingStatus)
                .stream()
                .map(BookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getMyBookingsByVehicleType(VehicleType vehicleType) {
        User user = getLoggedInUser();

        return bookingRepository
                .findByUserIdAndVehicleVehicleType(user.getId(), vehicleType)
                .stream()
                .map(BookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getMyBookingsByDateRange(LocalDate fromDate, LocalDate toDate) {
        User user = getLoggedInUser();

        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return bookingRepository
                .findByUserIdAndBookingTimeBetween(user.getId(), start, end)
                .stream()
                .map(BookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getBookingsByStatus(BookingStatus bookingStatus) {
        return bookingRepository.findByBookingStatus(bookingStatus)
                .stream()
                .map(BookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getBookingsByVehicleType(VehicleType vehicleType) {
        return bookingRepository.findByVehicleVehicleType(vehicleType)
                .stream()
                .map(BookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getBookingsByDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return bookingRepository.findByBookingTimeBetween(start, end)
                .stream()
                .map(BookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public BookingResponse startBooking(Long bookingId) {
        Booking booking = getBooking(bookingId);

        if (!booking.getBookingStatus().equals(BookingStatus.BOOKED)) {
            throw new BadRequestException("Only BOOKED booking can be started");
        }

        ParkingSlot slot = booking.getParkingSlot();

        booking.setBookingStatus(BookingStatus.ACTIVE);
        slot.setSlotStatus(SlotStatus.OCCUPIED);

        parkingSlotRepository.save(slot);
        Booking saved = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getUser(),
                "Vehicle entry successful for booking "
                        + booking.getBookingNumber(),
                NotificationType.CHECK_IN);

        return BookingMapper.mapToBookingResponse(saved);
    }

    @Override
    public BookingResponse completeBooking(Long bookingId) {
        Booking booking = getBooking(bookingId);

        if (!booking.getBookingStatus().equals(BookingStatus.ACTIVE)) {
            throw new BadRequestException("Only ACTIVE booking can be completed");
        }

        LocalDateTime actualExitTime = LocalDateTime.now();
        ParkingRate rate = parkingRateRepository
                .findByParkingAreaIdAndVehicleTypeAndDeletedFalse(
                        booking.getParkingArea().getId(),
                        booking.getVehicle().getVehicleType())
                .orElseThrow(() -> new ResourceNotFoundException("Parking rate not found"));

        BigDecimal hourlyRate = getHourlyRate(rate, actualExitTime);

        long extraMinutes = Duration.between(
                booking.getExpectedExitTime(),
                actualExitTime
        ).toMinutes();

        if (extraMinutes < 0) {
            extraMinutes = 0;
        }

        BigDecimal extraAmount =
                calculateExtraAmount(
                        rate,
                        booking.getExpectedExitTime(),
                        actualExitTime
                );

        booking.setActualExitTime(actualExitTime);
        booking.setExtraMinutes(extraMinutes);
        booking.setExtraAmount(extraAmount);
        booking.setTotalAmount(booking.getBaseAmount().add(extraAmount));
        booking.setBookingStatus(BookingStatus.COMPLETED);

        ParkingSlot slot = booking.getParkingSlot();
        slot.setSlotStatus(SlotStatus.AVAILABLE);

        parkingSlotRepository.save(slot);
        Booking saved = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getUser(),
                "Vehicle exit completed. Extra amount: ₹"
                        + booking.getExtraAmount()
                        + ". Total amount: ₹"
                        + booking.getTotalAmount(),
                NotificationType.CHECK_OUT);


        return BookingMapper.mapToBookingResponse(saved);
    }

    @Override
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = getBooking(bookingId);

        if (booking.getBookingStatus().equals(BookingStatus.CANCELLED)) {
            throw new BadRequestException("Booking is already cancelled");
        }

        if (booking.getBookingStatus().equals(BookingStatus.COMPLETED)) {
            throw new BadRequestException("Completed booking cannot be cancelled");
        }

        if (booking.getBookingStatus().equals(BookingStatus.ACTIVE)) {
            throw new BadRequestException("Active booking cannot be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        ParkingSlot slot = booking.getParkingSlot();
        slot.setSlotStatus(SlotStatus.AVAILABLE);

        parkingSlotRepository.save(slot);
        Booking saved = bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getUser(),
                "Your booking " + booking.getBookingNumber()
                        + " has been cancelled successfully.",
                NotificationType.BOOKING_CANCELLED);

        if (booking.getBookingNumber() != null) {
            emailService.sendBookingCancelledEmail(
                    booking.getUser().getEmail(),
                    booking.getBookingNumber(),
                    booking.getVehicle().getVehicleNumber(),
                    booking.getParkingArea().getAreaName(),
                    booking.getParkingSlot().getSlotNumber(),
                    FloorUtil.getFloorName(booking.getParkingSlot().getFloorNumber())
            );
        }

        return BookingMapper.mapToBookingResponse(saved);
    }


// Helper methods
    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id : " + bookingId));
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private BigDecimal getHourlyRate(ParkingRate rate, LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return rate.getWeekendRate();
        }
        return rate.getWeekdayRate();
    }

    private BigDecimal calculateExtraAmount(ParkingRate rate,
            LocalDateTime expectedExit, LocalDateTime actualExit) {

        if (!actualExit.isAfter(expectedExit)) {
            return BigDecimal.ZERO;
        }
        long totalExtraMinutes = ChronoUnit.MINUTES.between(expectedExit, actualExit);

        // 5-minute grace period
        if (totalExtraMinutes <= 5) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalExtraAmount = BigDecimal.ZERO;
        LocalDateTime current = expectedExit;

        while (current.isBefore(actualExit)) {
            LocalDateTime nextBoundary =
                    current.toLocalDate()
                            .plusDays(1)
                            .atStartOfDay();

            if (nextBoundary.isAfter(actualExit)) {
                nextBoundary = actualExit;
            }

            long minutes = ChronoUnit.MINUTES.between(current, nextBoundary);
            BigDecimal hourlyRate = getHourlyRate(rate, current);

            BigDecimal perMinuteRate =
                    hourlyRate.divide(
                            BigDecimal.valueOf(60),
                            2,
                            RoundingMode.HALF_UP);

            BigDecimal amount = perMinuteRate.multiply(BigDecimal.valueOf(minutes));
            totalExtraAmount = totalExtraAmount.add(amount);
            current = nextBoundary;
        }
        return totalExtraAmount.setScale(2, RoundingMode.HALF_UP);
    }

  /*  private BigDecimal calculateExtraAmount(BigDecimal hourlyRate, long extraMinutes) {

        if (extraMinutes <= 5) {
            return BigDecimal.ZERO;
        }

        BigDecimal perMinuteRate = hourlyRate.divide(
                BigDecimal.valueOf(60),
                2,
                RoundingMode.HALF_UP);

        return perMinuteRate
                .multiply(BigDecimal.valueOf(extraMinutes))
                .setScale(2, RoundingMode.HALF_UP);
    }

   */

}
