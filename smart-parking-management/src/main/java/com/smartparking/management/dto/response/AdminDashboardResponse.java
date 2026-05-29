package com.smartparking.management.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminDashboardResponse {


    private Long totalUsers;

    private Long totalParkingAreas;

    private Long totalSlots;
    private Long availableSlots;
    private Long reservedSlots;
    private Long occupiedSlots;

    private Long totalBookings;
    private Long activeBookings;
    private Long completedBookings;
    private Long cancelledBookings;

    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;














}
