package com.smartparking.management.mapper;

import com.smartparking.management.dto.response.BookingResponse;
import com.smartparking.management.entity.Booking;
import com.smartparking.management.util.FloorUtil;

public class BookingMapper {

    public static BookingResponse mapToBookingResponse(Booking booking) {

        BookingResponse response = new BookingResponse();

        response.setId(booking.getId());
        response.setBookingNumber(booking.getBookingNumber());
        response.setUserName(booking.getUser().getName());

        response.setVehicleNumber(booking.getVehicle().getVehicleNumber());
        response.setVehicleType(booking.getVehicle().getVehicleType());

        response.setSlotNumber(booking.getParkingSlot().getSlotNumber());
        response.setFloorNumber(booking.getParkingSlot().getFloorNumber());
        response.setFloorName(FloorUtil.getFloorName(booking.getParkingSlot().getFloorNumber()));

        response.setAreaName(booking.getParkingArea().getAreaName());

        response.setBookedHours(booking.getBookedHours());
        response.setExtraMinutes(booking.getExtraMinutes());

        response.setBaseAmount(booking.getBaseAmount());
        response.setExtraAmount(booking.getExtraAmount());
        response.setTotalAmount(booking.getTotalAmount());

        response.setBookingTime(booking.getBookingTime());
        response.setExpectedExitTime(booking.getExpectedExitTime());
        response.setActualExitTime(booking.getActualExitTime());

        response.setBookingStatus(booking.getBookingStatus());

        return response;
    }

}
