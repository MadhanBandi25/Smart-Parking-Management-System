package com.smartparking.management.service.impl;

import com.smartparking.management.dto.response.AdminDashboardResponse;
import com.smartparking.management.dto.response.ParkingOwnerDashboardResponse;
import com.smartparking.management.dto.response.UserDashboardResponse;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.Role;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.repository.*;
import com.smartparking.management.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public AdminDashboardResponse getAdminDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();

        response.setTotalUsers(userRepository.countByDeletedFalse());
        response.setTotalParkingAreas(parkingAreaRepository.countByDeletedFalse());

        response.setTotalSlots(parkingSlotRepository.countByDeletedFalse());
        response.setAvailableSlots(parkingSlotRepository.countBySlotStatusAndDeletedFalse(SlotStatus.AVAILABLE));
        response.setReservedSlots(parkingSlotRepository.countBySlotStatusAndDeletedFalse(SlotStatus.RESERVED));
        response.setOccupiedSlots(parkingSlotRepository.countBySlotStatusAndDeletedFalse(SlotStatus.OCCUPIED));

        response.setTotalBookings(bookingRepository.count());
        response.setActiveBookings(bookingRepository.countByBookingStatus(BookingStatus.ACTIVE));
        response.setCompletedBookings(bookingRepository.countByBookingStatus(BookingStatus.COMPLETED));
        response.setCancelledBookings(bookingRepository.countByBookingStatus(BookingStatus.CANCELLED));

        response.setTotalRevenue(paymentRepository.getTotalRevenue());
        response.setTodayRevenue(paymentRepository.getTodayRevenue());

        return response;
    }

    @Override
    public UserDashboardResponse getUserDashboard() {

        User user = getLoggedInUser();

        UserDashboardResponse response = new UserDashboardResponse();

        response.setTotalVehicles(vehicleRepository.countByUserIdAndDeletedFalse(user.getId()));
        response.setTotalBookings(bookingRepository.countByUserId(user.getId()));

        response.setActiveBookings(bookingRepository.countByUserIdAndBookingStatus(
                        user.getId(), BookingStatus.ACTIVE));

        response.setCompletedBookings(bookingRepository.countByUserIdAndBookingStatus(
                        user.getId(), BookingStatus.COMPLETED));

        response.setTotalAmountPaid(paymentRepository.getTotalAmountPaidByUserId(user.getId()));

        return response;
    }

    private User getLoggedInUser() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public ParkingOwnerDashboardResponse getParkingOwnerDashboard() {

        User owner = getLoggedInUser();

        if (!owner.getRole().equals(Role.PARKING_OWNER)) {
            throw new BadRequestException("Only parking owner can access this dashboard");
        }

        ParkingOwnerDashboardResponse response = new ParkingOwnerDashboardResponse();

        response.setTotalParkingAreas(parkingAreaRepository.countByOwnerIdAndDeletedFalse(owner.getId()));
        response.setTotalSlots(parkingSlotRepository.countByParkingAreaOwnerIdAndDeletedFalse(owner.getId()));

        response.setAvailableSlots(parkingSlotRepository.countByParkingAreaOwnerIdAndSlotStatusAndDeletedFalse(owner.getId(), SlotStatus.AVAILABLE));
        response.setReservedSlots(parkingSlotRepository.countByParkingAreaOwnerIdAndSlotStatusAndDeletedFalse(owner.getId(), SlotStatus.RESERVED));
        response.setOccupiedSlots(parkingSlotRepository.countByParkingAreaOwnerIdAndSlotStatusAndDeletedFalse(owner.getId(), SlotStatus.OCCUPIED));

        response.setTotalBookings(bookingRepository.countByParkingAreaOwnerId(owner.getId()));
        response.setActiveBookings(bookingRepository.countByParkingAreaOwnerIdAndBookingStatus(owner.getId(), BookingStatus.ACTIVE));

        response.setCompletedBookings(bookingRepository.countByParkingAreaOwnerIdAndBookingStatus(owner.getId(), BookingStatus.COMPLETED));
        response.setCancelledBookings(bookingRepository.countByParkingAreaOwnerIdAndBookingStatus(owner.getId(), BookingStatus.CANCELLED));

        response.setTotalRevenue(paymentRepository.getTotalRevenueByOwnerId(owner.getId()));

        return response;
    }
}
