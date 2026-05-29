package com.smartparking.management.service;

import com.smartparking.management.dto.request.VehicleRequest;
import com.smartparking.management.dto.response.VehicleResponse;
import com.smartparking.management.enums.VehicleType;

import java.util.List;

public interface VehicleService {


    VehicleResponse addVehicle(VehicleRequest request);
    VehicleResponse getVehicleById(Long id);
    List<VehicleResponse> getAllVehicles();

    List<VehicleResponse> getMyVehicles();
    List<VehicleResponse> searchByVehicleNumber(String vehicleNumber);
    List<VehicleResponse> filterByVehicleType(VehicleType vehicleType);

    void deleteVehicle(Long id);
    VehicleResponse restoreVehicle(Long id);

}
