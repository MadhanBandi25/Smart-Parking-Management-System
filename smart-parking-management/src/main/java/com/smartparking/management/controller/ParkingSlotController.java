package com.smartparking.management.controller;

import com.smartparking.management.dto.request.ParkingSlotGenerateRequest;
import com.smartparking.management.dto.response.ParkingSlotResponse;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.enums.VehicleType;
import com.smartparking.management.service.ParkingSlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-slots")
public class ParkingSlotController {

    @Autowired
    private ParkingSlotService parkingSlotService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateParkingSlots(@Valid @RequestBody ParkingSlotGenerateRequest request) {
        parkingSlotService.generateParkingSlots(request);
        return ResponseEntity.ok("Parking slots generated successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlotResponse> getParkingSlotById(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSlotService.getParkingSlotById(id));
    }

    @GetMapping
    public ResponseEntity<List<ParkingSlotResponse>> getAllParkingSlots() {
        return ResponseEntity.ok(parkingSlotService.getAllParkingSlots());
    }

    @GetMapping("/area/{parkingAreaId}")
    public ResponseEntity<List<ParkingSlotResponse>> getSlotsByParkingArea(@PathVariable Long parkingAreaId) {
        return ResponseEntity.ok(parkingSlotService.getSlotsByParkingArea(parkingAreaId));
    }

    @GetMapping("/status")
    public ResponseEntity<List<ParkingSlotResponse>> getSlotsByStatus(@RequestParam SlotStatus slotStatus) {
        return ResponseEntity.ok(parkingSlotService.getSlotsByStatus(slotStatus));
    }

    @GetMapping("/type")
    public ResponseEntity<List<ParkingSlotResponse>> getSlotsByVehicleType(
            @RequestParam VehicleType vehicleType) {
        return ResponseEntity.ok(parkingSlotService.getSlotsByVehicleType(vehicleType));
    }

    @GetMapping("/available/area/{parkingAreaId}")
    public ResponseEntity<List<ParkingSlotResponse>> getAvailableSlotsByArea(@PathVariable Long parkingAreaId) {
        return ResponseEntity.ok(parkingSlotService.getAvailableSlotsByArea(parkingAreaId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ParkingSlotResponse>> getAvailableSlotsByAreaAndVehicleType(
            @RequestParam Long parkingAreaId, @RequestParam VehicleType vehicleType) {

        return ResponseEntity.ok(parkingSlotService.getAvailableSlotsByAreaAndVehicleType(
                        parkingAreaId, vehicleType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingSlot(@PathVariable Long id) {
        parkingSlotService.deleteParkingSlot(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<ParkingSlotResponse> restoreParkingSlot(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSlotService.restoreParkingSlot(id));
    }

    @GetMapping("/search/slot")
    public ResponseEntity<ParkingSlotResponse> searchSlot(
            @RequestParam Long parkingAreaId, @RequestParam VehicleType vehicleType, @RequestParam String slotNumber) {
        ParkingSlotResponse response = parkingSlotService.searchSlot(parkingAreaId, vehicleType, slotNumber);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ParkingSlotResponse> updateSlotStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ParkingSlotResponse response = parkingSlotService.updateSlotStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
