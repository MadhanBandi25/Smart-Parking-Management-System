package com.smartparking.management.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookingRequest {

    @NotNull(message = "Vehicle id is required")
    private Long vehicleId;

    @NotNull(message = "Parking area id is required")
    private Long parkingAreaId;

    @NotNull(message = "Parking slot id is required")
    private Long parkingSlotId;

    @NotNull(message = "Booked hours is required")
    @Min(value = 1, message = "Minimum booking duration is 1 hour")
    private Integer bookedHours;
}
