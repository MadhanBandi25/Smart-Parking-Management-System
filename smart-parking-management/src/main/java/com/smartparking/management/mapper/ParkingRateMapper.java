package com.smartparking.management.mapper;


import com.smartparking.management.dto.request.ParkingRateRequest;
import com.smartparking.management.dto.response.ParkingRateResponse;
import com.smartparking.management.entity.ParkingArea;
import com.smartparking.management.entity.ParkingRate;

public class ParkingRateMapper {

    public static ParkingRate mapToParkingRateEntity(
            ParkingRateRequest request, ParkingArea parkingArea) {

        ParkingRate rate = new ParkingRate();

        rate.setVehicleType(request.getVehicleType());
        rate.setWeekdayRate(request.getWeekdayRate());
        rate.setWeekendRate(request.getWeekendRate());
        rate.setParkingArea(parkingArea);

        return rate;
    }

    public static ParkingRateResponse mapToParkingRateResponse(ParkingRate rate) {

        ParkingRateResponse response = new ParkingRateResponse();

        response.setId(rate.getId());
        response.setParkingAreaId(rate.getParkingArea().getId());
        response.setAreaName(rate.getParkingArea().getAreaName());
        response.setCity(rate.getParkingArea().getCity());
        response.setVehicleType(rate.getVehicleType());
        response.setWeekdayRate(rate.getWeekdayRate());
        response.setWeekendRate(rate.getWeekendRate());
        response.setCreatedAt(rate.getCreatedAt());

        return response;
    }

    public static void updateParkingRateEntity(ParkingRate rate, ParkingRateRequest request) {
        rate.setVehicleType(request.getVehicleType());
        rate.setWeekdayRate(request.getWeekdayRate());
        rate.setWeekendRate(request.getWeekendRate());
    }
}
