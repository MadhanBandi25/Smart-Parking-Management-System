package com.smartparking.management.service;

import com.smartparking.management.dto.request.ParkingRateRequest;
import com.smartparking.management.dto.response.ParkingRateResponse;

import java.util.List;

public interface ParkingRateService {

    ParkingRateResponse addParkingRate(ParkingRateRequest request);
    ParkingRateResponse getParkingRateById(Long id);
    List<ParkingRateResponse> getAllParkingRates();

    List<ParkingRateResponse> getRatesByParkingArea(Long parkingAreaId);
    ParkingRateResponse updateParkingRate(Long id, ParkingRateRequest request);

    void deleteParkingRate(Long id);
    ParkingRateResponse restoreParkingRate(Long id);
}
