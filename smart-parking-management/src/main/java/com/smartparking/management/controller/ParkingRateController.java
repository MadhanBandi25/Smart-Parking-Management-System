package com.smartparking.management.controller;

import com.smartparking.management.dto.request.ParkingRateRequest;
import com.smartparking.management.dto.response.ParkingRateResponse;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.ParkingRateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-rates")
public class ParkingRateController {



    @Autowired
    private ParkingRateService parkingRateService;

    @PostMapping
    public ResponseEntity<ParkingRateResponse> addParkingRate(
            @Valid @RequestBody ParkingRateRequest request) {

        ParkingRateResponse response = parkingRateService.addParkingRate(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingRateResponse> getParkingRateById(@PathVariable Long id) {
        ParkingRateResponse response = parkingRateService.getParkingRateById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ParkingRateResponse>> getAllParkingRates() {
        List<ParkingRateResponse> response = parkingRateService.getAllParkingRates();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/area/{parkingAreaId}")
    public ResponseEntity<List<ParkingRateResponse>> getRatesByParkingArea(
            @PathVariable Long parkingAreaId) {
        List<ParkingRateResponse> response = parkingRateService.getRatesByParkingArea(parkingAreaId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingRateResponse> updateParkingRate(
            @PathVariable Long id, @Valid @RequestBody ParkingRateRequest request) {
        ParkingRateResponse response = parkingRateService.updateParkingRate(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingRate(@PathVariable Long id) {
        parkingRateService.deleteParkingRate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<ParkingRateResponse> restoreParkingRate(@PathVariable Long id) {
        ParkingRateResponse response = parkingRateService.restoreParkingRate(id);
        return ResponseEntity.ok(response);
    }
}
