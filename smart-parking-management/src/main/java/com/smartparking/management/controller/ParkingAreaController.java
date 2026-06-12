package com.smartparking.management.controller;

import com.smartparking.management.dto.request.ParkingAreaRequest;
import com.smartparking.management.dto.response.ParkingAreaResponse;
import com.smartparking.management.enums.VehicleType;
import com.smartparking.management.service.ParkingAreaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-areas")
public class ParkingAreaController {

    @Autowired
    private ParkingAreaService parkingAreaService;

    @PostMapping
    public ResponseEntity<ParkingAreaResponse> addParkingArea(@Valid @RequestBody ParkingAreaRequest request) {
        ParkingAreaResponse response = parkingAreaService.addParkingArea(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingAreaResponse> getParkingAreaById(@PathVariable Long id) {
        ParkingAreaResponse response = parkingAreaService.getParkingAreaById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ParkingAreaResponse>> getAllParkingAreas() {
        List<ParkingAreaResponse> response = parkingAreaService.getAllParkingAreas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/city")
    public ResponseEntity<List<ParkingAreaResponse>> searchByCity(@RequestParam String city) {
        List<ParkingAreaResponse> response = parkingAreaService.searchByCity(city);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/address")
    public ResponseEntity<List<ParkingAreaResponse>> searchByAddress(@RequestParam String address) {
        List<ParkingAreaResponse> response = parkingAreaService.searchByAddress(address);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingAreaResponse> updateParkingArea(@PathVariable Long id, @Valid @RequestBody ParkingAreaRequest request) {
        ParkingAreaResponse response = parkingAreaService.updateParkingArea(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingArea(@PathVariable Long id) {
        parkingAreaService.deleteParkingArea(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<ParkingAreaResponse> restoreParkingArea(@PathVariable Long id) {
        ParkingAreaResponse response = parkingAreaService.restoreParkingArea(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{parkingAreaId}/vehicle-type")
    public ResponseEntity<Void> deleteVehicleType(
            @PathVariable Long parkingAreaId, @RequestParam VehicleType vehicleType) {
        parkingAreaService.deleteVehicleTypeFromArea(parkingAreaId, vehicleType);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{parkingAreaId}/vehicle-type/restore")
    public ResponseEntity<Void> restoreVehicleType(
            @PathVariable Long parkingAreaId, @RequestParam VehicleType vehicleType) {
        parkingAreaService.restoreVehicleTypeFromArea(parkingAreaId, vehicleType);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<ParkingAreaResponse>> getMyParkingAreas() {
        return ResponseEntity.ok(parkingAreaService.getMyParkingAreas());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ParkingAreaResponse>> getAllIncludingDeleted() {
        return ResponseEntity.ok(parkingAreaService.getAllIncludingDeletedForCurrentUser());
    }





}
