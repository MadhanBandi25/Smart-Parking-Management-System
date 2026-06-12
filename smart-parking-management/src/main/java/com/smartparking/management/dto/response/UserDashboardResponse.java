package com.smartparking.management.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserDashboardResponse {

    private Long totalVehicles;

    private Long totalBookings;
    private Long activeBookings;

    private Long completedBookings;
    private BigDecimal totalAmountPaid;
}
