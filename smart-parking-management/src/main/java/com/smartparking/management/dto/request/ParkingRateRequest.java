package com.smartparking.management.dto.request;

import com.smartparking.management.enums.VehicleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ParkingRateRequest {

    @NotNull(message = "Parking area id is required")
    private Long parkingAreaId;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Weekday rate is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Weekday rate must be greater than zero")
    private BigDecimal weekdayRate;

    @NotNull(message = "Weekend rate is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Weekend rate must be greater than zero")
    private BigDecimal weekendRate;

}
