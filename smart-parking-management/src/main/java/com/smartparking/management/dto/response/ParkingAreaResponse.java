package com.smartparking.management.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingAreaResponse {

    private Long id;
    private String areaName;
    private String address;
    private String city;

    private Integer bikeSlots;
    private Integer evBikeSlots;
    private Integer carSlots;
    private Integer evCarSlots;
    private Integer truckSlots;

    private Integer totalSlots;
    private Integer availableSlots;

    private LocalDateTime createdAt;
    private Boolean deleted;
}
