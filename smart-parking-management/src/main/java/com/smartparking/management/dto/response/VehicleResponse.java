package com.smartparking.management.dto.response;

import com.smartparking.management.enums.VehicleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleResponse {

    private Long id;
    private String vehicleNumber;
    private VehicleType vehicleType;

    private String brand;
    private String model;
    private String color;
    private String vehicleImageUrl;

    private String ownerName;
    private String ownerPhoneNumber;
    private LocalDateTime createdAt;

}
