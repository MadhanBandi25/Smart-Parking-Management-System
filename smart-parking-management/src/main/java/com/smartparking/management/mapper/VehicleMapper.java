package com.smartparking.management.mapper;

import com.smartparking.management.dto.request.VehicleRequest;
import com.smartparking.management.dto.response.VehicleResponse;
import com.smartparking.management.entity.Vehicle;

public class VehicleMapper {

    public static Vehicle mapToVehicleEntity(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleNumber(request.getVehicleNumber().trim().toUpperCase());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setBrand(formatName(request.getBrand().trim()));
        vehicle.setModel(formatName(request.getModel().trim()));
        vehicle.setColor(formatName(request.getColor().trim()));
        vehicle.setVehicleImageUrl(request.getVehicleImageUrl());

        return vehicle;
    }

    public static VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();

        response.setId(vehicle.getId());
        response.setVehicleNumber(vehicle.getVehicleNumber());
        response.setVehicleType(vehicle.getVehicleType());

        response.setBrand(vehicle.getBrand());
        response.setModel(vehicle.getModel());
        response.setColor(vehicle.getColor());
        response.setVehicleImageUrl(vehicle.getVehicleImageUrl());

        response.setOwnerName(vehicle.getUser().getName());
        response.setOwnerPhoneNumber(vehicle.getUser().getPhoneNumber());
        response.setCreatedAt(vehicle.getCreatedAt());
        return response;
    }

    public static void updateVehicleEntity(Vehicle vehicle, VehicleRequest request) {

        vehicle.setVehicleNumber(request.getVehicleNumber().trim().toUpperCase());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setBrand(formatName(request.getBrand().trim()));
        vehicle.setModel(formatName(request.getModel().trim()));
        vehicle.setColor(formatName(request.getColor().trim()));
        vehicle.setVehicleImageUrl(request.getVehicleImageUrl());
    }

    private static String formatName(String name) {
        String[] words = name.trim().toLowerCase().split("\\s+");

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

}
