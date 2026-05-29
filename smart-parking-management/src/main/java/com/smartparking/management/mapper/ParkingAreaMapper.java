package com.smartparking.management.mapper;

import com.smartparking.management.dto.request.ParkingAreaRequest;
import com.smartparking.management.dto.response.ParkingAreaResponse;
import com.smartparking.management.entity.ParkingArea;

public class ParkingAreaMapper {

    public static ParkingArea mapToParkingAreaEntity(ParkingAreaRequest request) {

        ParkingArea parkingArea = new ParkingArea();

        parkingArea.setAreaName(request.getAreaName().trim());
        parkingArea.setAddress(request.getAddress().trim());
        parkingArea.setCity(formatName(request.getCity().trim()));

        parkingArea.setBikeSlots(request.getBikeSlots());
        parkingArea.setEvBikeSlots(request.getEvBikeSlots());
        parkingArea.setCarSlots(request.getCarSlots());
        parkingArea.setEvCarSlots(request.getEvCarSlots());
        parkingArea.setTruckSlots(request.getTruckSlots());

        int totalSlots =  request.getBikeSlots()
                        + request.getEvBikeSlots()
                        + request.getCarSlots()
                        + request.getEvCarSlots()
                        + request.getTruckSlots();

        parkingArea.setTotalSlots(totalSlots);
        parkingArea.setAvailableSlots(totalSlots);

        return parkingArea;
    }

    public static ParkingAreaResponse mapToParkingAreaResponse(ParkingArea parkingArea) {

        ParkingAreaResponse response = new ParkingAreaResponse();

        response.setId(parkingArea.getId());
        response.setAreaName(parkingArea.getAreaName());
        response.setAddress(parkingArea.getAddress());
        response.setCity(parkingArea.getCity());

        response.setBikeSlots(parkingArea.getBikeSlots());
        response.setEvBikeSlots(parkingArea.getEvBikeSlots());
        response.setCarSlots(parkingArea.getCarSlots());
        response.setEvCarSlots(parkingArea.getEvCarSlots());
        response.setTruckSlots(parkingArea.getTruckSlots());

        response.setTotalSlots(parkingArea.getTotalSlots());
        response.setAvailableSlots(parkingArea.getAvailableSlots());
        response.setCreatedAt(parkingArea.getCreatedAt());

        return response;
    }

    public static void updateParkingAreaEntity(
            ParkingArea parkingArea, ParkingAreaRequest request) {

        parkingArea.setAreaName(request.getAreaName().trim());
        parkingArea.setAddress(request.getAddress().trim());
        parkingArea.setCity(formatName(request.getCity().trim()));

        parkingArea.setBikeSlots(request.getBikeSlots());
        parkingArea.setEvBikeSlots(request.getEvBikeSlots());
        parkingArea.setCarSlots(request.getCarSlots());
        parkingArea.setEvCarSlots(request.getEvCarSlots());
        parkingArea.setTruckSlots(request.getTruckSlots());

        int totalSlots =  request.getBikeSlots()
                        + request.getEvBikeSlots()
                        + request.getCarSlots()
                        + request.getEvCarSlots()
                        + request.getTruckSlots();

        parkingArea.setTotalSlots(totalSlots);
    }

    private static String formatName(String value) {
        String[] words = value.trim().toLowerCase().split("\\s+");

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0))
            ).append(word.substring(1)
            ).append(" ");
        }
        return sb.toString().trim();
    }
}
