package com.smartparking.management.service.impl;

import com.smartparking.management.dto.request.PaymentRequest;
import com.smartparking.management.dto.response.PaymentResponse;
import com.smartparking.management.entity.Booking;
import com.smartparking.management.entity.ParkingSlot;
import com.smartparking.management.entity.Payment;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.NotificationType;
import com.smartparking.management.enums.PaymentStatus;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.PaymentMapper;
import com.smartparking.management.repository.BookingRepository;
import com.smartparking.management.repository.ParkingSlotRepository;
import com.smartparking.management.repository.PaymentRepository;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.EmailService;
import com.smartparking.management.service.NotificationService;
import com.smartparking.management.service.PaymentService;
import com.smartparking.management.util.FloorUtil;
import com.smartparking.management.util.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public PaymentResponse makePayment(PaymentRequest request) {

        User user = getLoggedInUser();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("This booking does not belong to logged-in user");
        }

        if (!booking.getBookingStatus().equals(BookingStatus.PENDING_PAYMENT)) {
            throw new BadRequestException("Payment is allowed only for pending booking");
        }

        if (LocalDateTime.now().isAfter(booking.getPaymentExpiryTime())) {

            booking.setBookingStatus(BookingStatus.CANCELLED);

            ParkingSlot slot = booking.getParkingSlot();
            slot.setSlotStatus(SlotStatus.AVAILABLE);

            parkingSlotRepository.save(slot);
            bookingRepository.save(booking);

            throw new BadRequestException("Payment time expired. Slot released.");
        }

        if (paymentRepository.existsByBookingId(booking.getId())) {
            throw new BadRequestException("Payment already exists for this booking");
        }

        Payment payment = new Payment();

        payment.setPaymentNumber(generatePaymentNumber(request.getPaymentMethod().name()));
        payment.setTransactionId(generateTransactionId());
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setBooking(booking);

        booking.setBookingNumber(generateBookingNumber(booking));
        booking.setBookingStatus(BookingStatus.BOOKED);

        bookingRepository.save(booking);

        Payment saved = paymentRepository.save(payment);

        emailService.sendBookingConfirmedEmail(
                booking.getUser().getEmail(),
                booking.getBookingNumber(),
                booking.getVehicle().getVehicleNumber(),
                booking.getParkingArea().getAreaName(),
                booking.getParkingSlot().getSlotNumber(),
                FloorUtil.getFloorName(booking.getParkingSlot().getFloorNumber()),
                booking.getBookingTime().toString(),
                booking.getExpectedExitTime().toString()
        );

        notificationService.createNotification(
                booking.getUser(),
                "Payment successful. Your booking is confirmed.",
                NotificationType.PAYMENT_SUCCESS
        );
        notificationService.createNotification(
                booking.getUser(),
                "Booking confirmed successfully. Booking Number: "
                        + booking.getBookingNumber(),
                NotificationType.BOOKING_CONFIRMED
        );


        PaymentResponse response = PaymentMapper.mapToPaymentResponse(saved);
        response.setQrCodeBase64(QrCodeUtil.generateQrCodeBase64(booking.getBookingNumber()));
        return response;

    }

    @Override
    @Transactional
    public PaymentResponse markPaymentFailed(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getBookingStatus().equals(BookingStatus.PENDING_PAYMENT)) {
            throw new BadRequestException("Only pending payment booking can be failed");
        }
        if (paymentRepository.existsByBookingId(booking.getId())) {
            throw new BadRequestException("Payment already exists for this booking");
        }

        Payment payment = new Payment();

        payment.setPaymentNumber(generatePaymentNumber("FAILED"));
        payment.setTransactionId(generateTransactionId());
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(null);
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setBooking(booking);

        booking.setBookingStatus(BookingStatus.CANCELLED);

        ParkingSlot slot = booking.getParkingSlot();
        slot.setSlotStatus(SlotStatus.AVAILABLE);

        parkingSlotRepository.save(slot);
        bookingRepository.save(booking);

        Payment saved = paymentRepository.save(payment);

        return PaymentMapper.mapToPaymentResponse(saved);
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return PaymentMapper.mapToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByNumber(String paymentNumber) {
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        return PaymentMapper.mapToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking"));

        return PaymentMapper.mapToPaymentResponse(payment);
    }

    @Override
    public List<PaymentResponse> getMyPayments() {
        User user = getLoggedInUser();

        return paymentRepository.findByBookingUserId(user.getId())
                .stream()
                .map(PaymentMapper::mapToPaymentResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getMyPaymentsByDateRange(LocalDate fromDate, LocalDate toDate) {
        User user = getLoggedInUser();

        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        if (start.isBefore(user.getCreatedAt())) {
            start = user.getCreatedAt();
        }

        return paymentRepository
                .findByBookingUserIdAndPaymentTimeBetween(user.getId(), start, end)
                .stream()
                .map(PaymentMapper::mapToPaymentResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::mapToPaymentResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus paymentStatus) {
        return paymentRepository.findByPaymentStatus(paymentStatus)
                .stream()
                .map(PaymentMapper::mapToPaymentResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        return paymentRepository.findByPaymentTimeBetween(start, end)
                .stream()
                .map(PaymentMapper::mapToPaymentResponse)
                .toList();
    }
    // Helper methods


    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
//
    private String generatePaymentNumber(String method) {

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd"));
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

        String milliSeconds = String.format(
                "%03d", LocalTime.now().getNano() / 1_000_000);

        String random = UUID.randomUUID()
                .toString()
                .substring(0, 4)
                .toUpperCase();

        return "PAY" + method.charAt(0) + date + time + milliSeconds + random;
    }




    private String generateTransactionId() {
        String transactionId;
        do {
            transactionId = "TXN" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();

        } while (paymentRepository.findByTransactionId(transactionId).isPresent());
        return transactionId;
    }

    private String generateBookingNumber(Booking booking) {
        String bookingNumber;

        do {
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd"));
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

            String milliSeconds = String.format("%03d", LocalTime.now().getNano() / 1_000_000);

            String type = switch (booking.getVehicle().getVehicleType()) {
                case BIKE -> "BK";
                case EV_BIKE -> "EBK";
                case CAR -> "CAR";
                case EV_CAR -> "ECAR";
                case TRUCK -> "TRK";
            };

            String random = UUID.randomUUID()
                    .toString()
                    .substring(0, 2)
                    .toUpperCase();

            bookingNumber = "BK" + type + date + time + milliSeconds + random;

        } while (bookingRepository.findByBookingNumber(bookingNumber).isPresent());

        return bookingNumber;
    }
}
