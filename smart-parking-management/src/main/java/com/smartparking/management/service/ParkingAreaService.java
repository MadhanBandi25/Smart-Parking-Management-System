package com.smartparking.management.service;

import com.smartparking.management.dto.request.ParkingAreaRequest;
import com.smartparking.management.dto.response.ParkingAreaResponse;
import com.smartparking.management.enums.VehicleType;

import java.util.List;

public interface ParkingAreaService {

    ParkingAreaResponse addParkingArea(ParkingAreaRequest request);
    ParkingAreaResponse getParkingAreaById(Long id);
    List<ParkingAreaResponse> getAllParkingAreas();

    List<ParkingAreaResponse> searchByCity(String city);
    List<ParkingAreaResponse> searchByAddress(String address);

    ParkingAreaResponse updateParkingArea(Long id, ParkingAreaRequest request);

    void deleteParkingArea(Long id);
    ParkingAreaResponse restoreParkingArea(Long id);

    void deleteVehicleTypeFromArea(Long parkingAreaId, VehicleType vehicleType);
    void restoreVehicleTypeFromArea(Long parkingAreaId, VehicleType vehicleType);

    List<ParkingAreaResponse> getMyParkingAreas();
    List<ParkingAreaResponse> getAllIncludingDeletedForCurrentUser();
}
