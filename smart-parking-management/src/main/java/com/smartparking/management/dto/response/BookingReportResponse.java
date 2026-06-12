package com.smartparking.management.dto.response;

import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.VehicleType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingReportResponse {

    private String bookingNumber;
    private String userName;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private String areaName;
    private String slotNumber;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingTime;
}
