package com.smartparking.management.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class AnalyticsResponse {

    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;

    private Long totalBookings;
    private Long completedBookings;
    private Long cancelledBookings;
    private Long activeBookings;

    private Map<String, Long> vehicleTypeBookings;
    private Map<String, Long> bookingStatusCount;
    private Map<String, BigDecimal> areaWiseRevenue;
}
