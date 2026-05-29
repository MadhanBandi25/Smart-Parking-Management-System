package com.smartparking.management.dto.response;

import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.enums.VehicleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingSlotResponse {

    private Long id;
    private String slotNumber;

    private Integer floorNumber;
    private String floorName;

    private VehicleType vehicleType;
    private SlotStatus slotStatus;

    private Long parkingAreaId;
    private String areaName;
    private String city;

    private LocalDateTime createdAt;

}
