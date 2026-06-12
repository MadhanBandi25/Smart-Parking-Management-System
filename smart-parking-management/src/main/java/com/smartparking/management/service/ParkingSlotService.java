package com.smartparking.management.service;

import com.smartparking.management.dto.request.ParkingSlotGenerateRequest;
import com.smartparking.management.dto.response.ParkingSlotResponse;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.enums.VehicleType;

import java.util.List;

public interface ParkingSlotService {


    void generateParkingSlots(ParkingSlotGenerateRequest request);

    ParkingSlotResponse getParkingSlotById(Long id);
    List<ParkingSlotResponse> getAllParkingSlots();
    List<ParkingSlotResponse> getSlotsByParkingArea(Long parkingAreaId);

    List<ParkingSlotResponse> getSlotsByStatus(SlotStatus slotStatus);
    List<ParkingSlotResponse> getSlotsByVehicleType(VehicleType vehicleType);
    List<ParkingSlotResponse> getAvailableSlotsByArea(Long parkingAreaId);

    ParkingSlotResponse searchSlot(Long parkingAreaId, VehicleType vehicleType, String slotNumber);
    List<ParkingSlotResponse> getAvailableSlotsByAreaAndVehicleType(
            Long parkingAreaId, VehicleType vehicleType);

    void deleteParkingSlot(Long id);
    ParkingSlotResponse restoreParkingSlot(Long id);

    ParkingSlotResponse updateSlotStatus(Long id, String status);
}
