package com.smartparking.management.service.impl;

import com.smartparking.management.dto.request.ParkingRateRequest;
import com.smartparking.management.dto.response.ParkingRateResponse;
import com.smartparking.management.entity.ParkingArea;
import com.smartparking.management.entity.ParkingRate;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.Role;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.ParkingRateMapper;
import com.smartparking.management.repository.ParkingAreaRepository;
import com.smartparking.management.repository.ParkingRateRepository;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.ParkingRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingRateServiceImpl implements ParkingRateService {

    @Autowired
    private ParkingRateRepository parkingRateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

    @Override
    public ParkingRateResponse addParkingRate(ParkingRateRequest request) {
        ParkingArea parkingArea = getActiveParkingArea(request.getParkingAreaId());
        validateParkingAreaOwner(parkingArea);

        if (parkingRateRepository.existsByParkingAreaIdAndVehicleTypeAndDeletedFalse(
                parkingArea.getId(), request.getVehicleType())) {
            throw new BadRequestException("Parking rate already exists for this vehicle type");
        }

        ParkingRate rate = ParkingRateMapper.mapToParkingRateEntity(request, parkingArea);
        ParkingRate saved = parkingRateRepository.save(rate);
        return ParkingRateMapper.mapToParkingRateResponse(saved);
    }

    @Override
    public ParkingRateResponse getParkingRateById(Long id) {
        ParkingRate rate = getActiveParkingRate(id);
        validateParkingAreaOwner(rate.getParkingArea());
        return ParkingRateMapper.mapToParkingRateResponse(rate);
    }

    @Override
    public List<ParkingRateResponse> getAllParkingRates() {
        return parkingRateRepository.findAllByDeletedFalse()
                .stream()
                .map(ParkingRateMapper::mapToParkingRateResponse)
                .toList();
    }

    @Override
    public List<ParkingRateResponse> getRatesByParkingArea(Long parkingAreaId) {
        ParkingArea parkingArea = getActiveParkingArea(parkingAreaId);
        validateParkingAreaOwner(parkingArea);

        return parkingRateRepository.findByParkingAreaIdAndDeletedFalse(parkingAreaId)
                .stream()
                .map(ParkingRateMapper::mapToParkingRateResponse)
                .toList();
    }

    @Override
    public ParkingRateResponse updateParkingRate(Long id, ParkingRateRequest request) {
        ParkingRate rate = getActiveParkingRate(id);

        ParkingArea parkingArea = getActiveParkingArea(request.getParkingAreaId());
        validateParkingAreaOwner(parkingArea);

        validateDuplicateRateForUpdate(rate, request);
        ParkingRateMapper.updateParkingRateEntity(rate, request);

        rate.setParkingArea(parkingArea);

        ParkingRate saved = parkingRateRepository.save(rate);
        return ParkingRateMapper.mapToParkingRateResponse(saved);
    }

    @Override
    public void deleteParkingRate(Long id) {
        ParkingRate rate = getActiveParkingRate(id);
        validateParkingAreaOwner(rate.getParkingArea());

        if (rate.getDeleted()) {
            throw new BadRequestException("Parking rate is already deleted");
        }

        rate.setDeleted(true);
        parkingRateRepository.save(rate);
    }

    @Override
    public ParkingRateResponse restoreParkingRate(Long id) {
        ParkingRate rate = getParkingRate(id);
        validateParkingAreaOwner(rate.getParkingArea());

        if (!rate.getDeleted()) {
            throw new BadRequestException("Parking rate is already active");
        }

        rate.setDeleted(false);
        ParkingRate saved = parkingRateRepository.save(rate);
        return ParkingRateMapper.mapToParkingRateResponse(saved);
    }

    private ParkingRate getActiveParkingRate(Long id) {
        return parkingRateRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking rate not found with id : " + id));
    }

    private ParkingRate getParkingRate(Long id) {
        return parkingRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking rate not found with id : " + id));
    }

    private ParkingArea getActiveParkingArea(Long id) {
        return parkingAreaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking area not found with id : " + id));
    }

    private void validateDuplicateRateForUpdate(
            ParkingRate rate, ParkingRateRequest request) {

        boolean sameParkingArea = rate.getParkingArea().getId().equals(request.getParkingAreaId());
        boolean sameVehicleType = rate.getVehicleType().equals(request.getVehicleType());

        if (!(sameParkingArea && sameVehicleType)
                && parkingRateRepository.existsByParkingAreaIdAndVehicleTypeAndDeletedFalse(
                request.getParkingAreaId(), request.getVehicleType())) {
            throw new BadRequestException("Parking rate already exists for this vehicle type");
        }
    }



    private User getLoggedInUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));
    }

    private void validateParkingAreaOwner(ParkingArea parkingArea) {
        User user = getLoggedInUser();
        if (user.getRole().equals(Role.ADMIN)) {
            return;
        }
        if (parkingArea.getOwner() == null ||
                !parkingArea.getOwner().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot access this parking area");
        }
    }
}
