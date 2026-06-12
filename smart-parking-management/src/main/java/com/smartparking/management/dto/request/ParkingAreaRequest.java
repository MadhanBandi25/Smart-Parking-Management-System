package com.smartparking.management.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ParkingAreaRequest {

    @NotBlank(message = "Area name is required")
    @Size(min = 2, max = 100, message = "Area name must be between 2 and 100 characters")
    private String areaName;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 150, message = "Address must be between 5 and 150 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;

    @NotNull(message = "Bike slots is required")
    @Min(value = 0, message = "Bike slots cannot be negative")
    private Integer bikeSlots;

    @NotNull(message = "EV bike slots is required")
    @Min(value = 0, message = "EV bike slots cannot be negative")
    private Integer evBikeSlots;

    @NotNull(message = "Car slots is required")
    @Min(value = 0, message = "Car slots cannot be negative")
    private Integer carSlots;

    @NotNull(message = "EV car slots is required")
    @Min(value = 0, message = "EV car slots cannot be negative")
    private Integer evCarSlots;

    @NotNull(message = "Truck slots is required")
    @Min(value = 0, message = "Truck slots cannot be negative")
    private Integer truckSlots;
}
