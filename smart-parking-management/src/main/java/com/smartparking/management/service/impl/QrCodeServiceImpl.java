package com.smartparking.management.service.impl;

import com.smartparking.management.dto.response.BookingResponse;
import com.smartparking.management.dto.response.QrCodeResponse;
import com.smartparking.management.entity.Booking;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.BookingMapper;
import com.smartparking.management.repository.BookingRepository;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.QrCodeService;
import com.smartparking.management.util.FloorUtil;
import com.smartparking.management.util.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QrCodeServiceImpl implements QrCodeService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public QrCodeResponse generateQrCode(String bookingNumber) {

        User user = getLoggedInUser();

        Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("This booking does not belong to logged-in user");
        }

        if (!booking.getBookingStatus().equals(BookingStatus.BOOKED)
                && !booking.getBookingStatus().equals(BookingStatus.ACTIVE)) {
            throw new BadRequestException("QR code can be generated only after payment success");
        }

        String qrBase64 = QrCodeUtil.generateQrCodeBase64(booking.getBookingNumber());

        QrCodeResponse response = new QrCodeResponse();

        response.setBookingId(booking.getId());
        response.setBookingNumber(booking.getBookingNumber());
        response.setQrCodeBase64(qrBase64);
        response.setUserName(booking.getUser().getName());
        response.setVehicleNumber(booking.getVehicle().getVehicleNumber());
        response.setVehicleType(booking.getVehicle().getVehicleType());
        response.setSlotNumber(booking.getParkingSlot().getSlotNumber());
        response.setFloorNumber(booking.getParkingSlot().getFloorNumber());
        response.setFloorName(FloorUtil.getFloorName(booking.getParkingSlot().getFloorNumber()));
        response.setAreaName(booking.getParkingArea().getAreaName());
        response.setBookingStatus(booking.getBookingStatus());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse verifyQrCode(String bookingNumber) {

        Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR code"));

        if (!booking.getBookingStatus().equals(BookingStatus.BOOKED)
                && !booking.getBookingStatus().equals(BookingStatus.ACTIVE)) {
            throw new BadRequestException("QR code is invalid or expired");
        }

        return BookingMapper.mapToBookingResponse(booking);
    }


    private User getLoggedInUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }






}
