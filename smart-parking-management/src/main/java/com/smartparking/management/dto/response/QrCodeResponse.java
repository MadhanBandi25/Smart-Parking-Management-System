package com.smartparking.management.dto.response;

import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.VehicleType;
import lombok.Data;

@Data
public class QrCodeResponse {

    private Long bookingId;
    private String bookingNumber;
    private String qrCodeBase64;

    private String userName;
    private String vehicleNumber;
    private VehicleType vehicleType;

    private String slotNumber;
    private Integer floorNumber;
    private String floorName;

    private String areaName;
    private BookingStatus bookingStatus;

}
