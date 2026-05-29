package com.smartparking.management.dto.response;

import com.smartparking.management.enums.VehicleType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ParkingRateResponse {

    private Long id;
    private Long parkingAreaId;
    private String areaName;
    private String city;
    private VehicleType vehicleType;

    private BigDecimal weekdayRate;
    private BigDecimal weekendRate;
    private LocalDateTime createdAt;
}
