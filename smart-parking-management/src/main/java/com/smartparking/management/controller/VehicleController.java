package com.smartparking.management.controller;

import com.smartparking.management.dto.request.VehicleRequest;
import com.smartparking.management.dto.response.VehicleResponse;
import com.smartparking.management.enums.VehicleType;
import com.smartparking.management.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;


    @PostMapping
    public ResponseEntity<VehicleResponse> addVehicle(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.addVehicle(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        List<VehicleResponse> response = vehicleService.getAllVehicles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-vehicles")
    public ResponseEntity<List<VehicleResponse>> getMyVehicles() {
        List<VehicleResponse> response = vehicleService.getMyVehicles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<VehicleResponse>> searchByVehicleNumber(@RequestParam String vehicleNumber) {
        List<VehicleResponse> response = vehicleService.searchByVehicleNumber(vehicleNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type")
    public ResponseEntity<List<VehicleResponse>> filterByVehicleType(@RequestParam VehicleType vehicleType) {
        List<VehicleResponse> response = vehicleService.filterByVehicleType(vehicleType);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<VehicleResponse> restoreVehicle(@PathVariable Long id) {
        VehicleResponse response = vehicleService.restoreVehicle(id);
        return ResponseEntity.ok(response);
    }

}
