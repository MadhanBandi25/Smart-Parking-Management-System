package com.smartparking.management.service.impl;

import com.smartparking.management.dto.request.ParkingAreaRequest;
import com.smartparking.management.dto.response.ParkingAreaResponse;
import com.smartparking.management.entity.ParkingArea;
import com.smartparking.management.entity.ParkingRate;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.Role;
import com.smartparking.management.enums.VehicleType;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.ParkingAreaMapper;
import com.smartparking.management.repository.ParkingAreaRepository;
import com.smartparking.management.repository.ParkingRateRepository;
import com.smartparking.management.repository.ParkingSlotRepository;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.ParkingAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingAreaServiceImpl implements ParkingAreaService {

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    @Autowired
    private ParkingRateRepository parkingRateRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public ParkingAreaResponse addParkingArea(ParkingAreaRequest request) {

        validateSlotCount(request);
        validateDuplicateArea(request);

        User loggedInUser = getLoggedInUser();

        ParkingArea parkingArea = ParkingAreaMapper.mapToParkingAreaEntity(request);

        if (loggedInUser.getRole().equals(Role.PARKING_OWNER)) {
            parkingArea.setOwner(loggedInUser);
        }
        ParkingArea saved = parkingAreaRepository.save(parkingArea);
        return ParkingAreaMapper.mapToParkingAreaResponse(saved);

    }

    @Override
    public ParkingAreaResponse getParkingAreaById(Long id) {
        ParkingArea parkingArea = getActiveParkingArea(id);
        validateParkingAreaOwner(parkingArea);

        return ParkingAreaMapper.mapToParkingAreaResponse(parkingArea);
    }

    @Override
    public List<ParkingAreaResponse> getAllParkingAreas() {
        return parkingAreaRepository.findAllByDeletedFalse()
                .stream()
                .map(ParkingAreaMapper::mapToParkingAreaResponse)
                .toList();
    }

    @Override
    public List<ParkingAreaResponse> searchByCity(String city) {
        return parkingAreaRepository
                .findByCityContainingIgnoreCaseAndDeletedFalse(city)
                .stream()
                .map(ParkingAreaMapper::mapToParkingAreaResponse)
                .toList();
    }

    @Override
    public List<ParkingAreaResponse> searchByAddress(String address) {
        return parkingAreaRepository
                .findByAddressContainingIgnoreCaseAndDeletedFalse(address)
                .stream()
                .map(ParkingAreaMapper::mapToParkingAreaResponse)
                .toList();
    }

    @Override
    public ParkingAreaResponse updateParkingArea(Long id, ParkingAreaRequest request) {

        ParkingArea parkingArea = getActiveParkingArea(id);
        validateParkingAreaOwner(parkingArea);

        validateSlotCount(request);
        validateDuplicateAreaForUpdate(parkingArea, request);

        ParkingAreaMapper.updateParkingAreaEntity(parkingArea, request);
        ParkingArea saved = parkingAreaRepository.save(parkingArea);
        return ParkingAreaMapper.mapToParkingAreaResponse(saved);
    }

    @Override
    public void deleteParkingArea(Long id) {

        ParkingArea parkingArea = getActiveParkingArea(id);
        validateParkingAreaOwner(parkingArea);

        if (parkingArea.getDeleted()) {
            throw new BadRequestException("Parking area is already deleted");
        }

        parkingArea.setDeleted(true);
        parkingAreaRepository.save(parkingArea);
    }

    @Override
    public ParkingAreaResponse restoreParkingArea(Long id) {

        ParkingArea parkingArea = getParkingArea(id);
        validateParkingAreaOwner(parkingArea);

        if (!parkingArea.getDeleted()) {
            throw new BadRequestException("Parking area is already active");
        }

        parkingArea.setDeleted(false);
        ParkingArea saved = parkingAreaRepository.save(parkingArea);
        return ParkingAreaMapper.mapToParkingAreaResponse(saved);
    }


    @Override
    public void deleteVehicleTypeFromArea(Long parkingAreaId, VehicleType vehicleType) {

        ParkingArea parkingArea = getActiveParkingArea(parkingAreaId);
        validateParkingAreaOwner(parkingArea);
        parkingSlotRepository
                .findByParkingAreaIdAndVehicleTypeAndDeletedFalse(
                        parkingAreaId, vehicleType)
                .forEach(slot -> {slot.setDeleted(true);
                    parkingSlotRepository.save(slot);});

        ParkingRate rate = parkingRateRepository
                        .findByParkingAreaIdAndVehicleTypeAndDeletedFalse(
                                parkingAreaId, vehicleType)
                        .orElseThrow(() -> new ResourceNotFoundException("Rate not found"));

        rate.setDeleted(true);
        parkingRateRepository.save(rate);
    }

    @Override
    public void restoreVehicleTypeFromArea(Long parkingAreaId, VehicleType vehicleType) {

        ParkingArea parkingArea = getParkingArea(parkingAreaId);
        validateParkingAreaOwner(parkingArea);

        parkingSlotRepository
                .findByParkingAreaIdAndVehicleType(parkingAreaId, vehicleType)
                .forEach(slot -> {slot.setDeleted(false);
                    parkingSlotRepository.save(slot);});

        ParkingRate rate = parkingRateRepository
                        .findByParkingAreaIdAndVehicleType(
                                parkingAreaId, vehicleType)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Rate not found"));
        rate.setDeleted(false);
        parkingRateRepository.save(rate);
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

    @Override
    public List<ParkingAreaResponse> getMyParkingAreas() {
        User owner = getLoggedInUser();
        return parkingAreaRepository
                .findByOwnerIdAndDeletedFalse(owner.getId())
                .stream()
                .map(ParkingAreaMapper::mapToParkingAreaResponse)
                .toList();
    }

    @Override
    public List<ParkingAreaResponse> getAllIncludingDeletedForCurrentUser() {
        User user = getLoggedInUser();
        if (user.getRole().equals(Role.ADMIN)) {
            return parkingAreaRepository.findAll()
                    .stream()
                    .map(ParkingAreaMapper::mapToParkingAreaResponse)
                    .toList();
        }
        return parkingAreaRepository.findByOwnerId(user.getId())
                .stream()
                .map(ParkingAreaMapper::mapToParkingAreaResponse)
                .toList();
    }








    private ParkingArea getActiveParkingArea(Long id) {
        return parkingAreaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Parking area not found with id : " + id));
    }

    private ParkingArea getParkingArea(Long id) {
        return parkingAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Parking area not found with id : " + id));
    }

    private void validateDuplicateArea(ParkingAreaRequest request) {
        String areaName = normalize(request.getAreaName());
        String address = normalize(request.getAddress());

        if (parkingAreaRepository
                .existsByAreaNameIgnoreCaseAndAddressIgnoreCaseAndDeletedFalse(areaName, address)) {
            throw new BadRequestException("Parking area already exists");
        }
    }

    private void validateDuplicateAreaForUpdate(ParkingArea parkingArea, ParkingAreaRequest request) {

        String areaName = normalize(request.getAreaName());
        String address = normalize(request.getAddress());

        boolean sameAreaName = parkingArea.getAreaName().equalsIgnoreCase(areaName);
        boolean sameAddress = parkingArea.getAddress().equalsIgnoreCase(address);

        if (!(sameAreaName && sameAddress) && parkingAreaRepository
                .existsByAreaNameIgnoreCaseAndAddressIgnoreCaseAndDeletedFalse(
                        areaName, address)) {
            throw new BadRequestException("Parking area already exists");
        }
    }

    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private void validateSlotCount(ParkingAreaRequest request) {

        int totalSlots =  request.getBikeSlots()
                        + request.getEvBikeSlots()
                        + request.getCarSlots()
                        + request.getEvCarSlots()
                        + request.getTruckSlots();

        if (totalSlots <= 0) {
            throw new BadRequestException("At least one slot category must be greater than 0");
        }
    }

}
