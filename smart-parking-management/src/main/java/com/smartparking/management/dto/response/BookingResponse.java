package com.smartparking.management.dto.response;

import com.smartparking.management.enums.BookingStatus;
import com.smartparking.management.enums.VehicleType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponse {


    private Long id;
    private String bookingNumber;

    private String userName;
    private String vehicleNumber;
    private VehicleType vehicleType;

    private String slotNumber;
    private Integer floorNumber;
    private String floorName;
    private String areaName;

    private Integer bookedHours;
    private Long extraMinutes;

    private BigDecimal baseAmount;
    private BigDecimal extraAmount;
    private BigDecimal totalAmount;

    private LocalDateTime bookingTime;
    private LocalDateTime expectedExitTime;
    private LocalDateTime actualExitTime;

    private BookingStatus bookingStatus;

}
