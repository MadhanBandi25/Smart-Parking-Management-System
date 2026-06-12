package com.smartparking.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkingSlotGenerateRequest {

    @NotNull(message = "Parking area id is required")
    private Long parkingAreaId;

    @NotNull(message = "Bike floor is required")
    private Integer bikeFloor;

    @NotNull(message = "EV Bike floor is required")
    private Integer evBikeFloor;

    @NotNull(message = "Car floor is required")
    private Integer carFloor;

    @NotNull(message = "EV Car floor is required")
    private Integer evCarFloor;

    @NotNull(message = "Truck floor is required")
    private Integer truckFloor;
}
