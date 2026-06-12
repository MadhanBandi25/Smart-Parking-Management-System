package com.smartparking.management.mapper;

import com.smartparking.management.dto.response.ParkingSlotResponse;
import com.smartparking.management.entity.ParkingSlot;
import com.smartparking.management.util.FloorUtil;

public class ParkingSlotMapper {

    public static ParkingSlotResponse mapToParkingSlotResponse(ParkingSlot slot) {

        ParkingSlotResponse response = new ParkingSlotResponse();

        response.setId(slot.getId());
        response.setSlotNumber(slot.getSlotNumber());
        response.setFloorNumber(slot.getFloorNumber());
        response.setFloorName(FloorUtil.getFloorName(slot.getFloorNumber()));

        response.setVehicleType(slot.getVehicleType());
        response.setSlotStatus(slot.getSlotStatus());

        response.setParkingAreaId(slot.getParkingArea().getId());
        response.setAreaName(slot.getParkingArea().getAreaName());
        response.setCity(slot.getParkingArea().getCity());
        response.setCreatedAt(slot.getCreatedAt());
        return response;
    }

}
