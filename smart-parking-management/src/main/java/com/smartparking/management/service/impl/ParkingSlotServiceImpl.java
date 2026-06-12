package com.smartparking.management.service.impl;

import com.smartparking.management.dto.request.ParkingSlotGenerateRequest;
import com.smartparking.management.dto.response.ParkingSlotResponse;
import com.smartparking.management.entity.ParkingArea;
import com.smartparking.management.entity.ParkingSlot;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.Role;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.enums.VehicleType;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.ParkingSlotMapper;
import com.smartparking.management.repository.ParkingAreaRepository;
import com.smartparking.management.repository.ParkingSlotRepository;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.ParkingSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSlotServiceImpl implements ParkingSlotService {

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingAreaRepository parkingAreaRepository;

    @Override
    public void generateParkingSlots(ParkingSlotGenerateRequest request) {

        ParkingArea parkingArea = getActiveParkingArea(request.getParkingAreaId());
        validateParkingAreaOwner(parkingArea);

        if (parkingSlotRepository.existsByParkingAreaIdAndDeletedFalse(
                parkingArea.getId())) {
            throw new BadRequestException("Parking slots already generated for this parking area");
        }

        createSlots(
                parkingArea,
                "B",
                parkingArea.getBikeSlots(),
                VehicleType.BIKE,
                request.getBikeFloor()
        );

        createSlots(
                parkingArea,
                "EB",
                parkingArea.getEvBikeSlots(),
                VehicleType.EV_BIKE,
                request.getEvBikeFloor()
        );

        createSlots(
                parkingArea,
                "C",
                parkingArea.getCarSlots(),
                VehicleType.CAR,
                request.getCarFloor()
        );

        createSlots(
                parkingArea,
                "EC",
                parkingArea.getEvCarSlots(),
                VehicleType.EV_CAR,
                request.getEvCarFloor()
        );

        createSlots(
                parkingArea,
                "T",
                parkingArea.getTruckSlots(),
                VehicleType.TRUCK,
                request.getTruckFloor()
        );
    }

    @Override
    public ParkingSlotResponse getParkingSlotById(Long id) {
        ParkingSlot parkingSlot = getActiveParkingSlot(id);
        validateParkingAreaOwner(parkingSlot.getParkingArea());
        return ParkingSlotMapper.mapToParkingSlotResponse(parkingSlot);
    }

    @Override
    public List<ParkingSlotResponse> getAllParkingSlots() {
        return parkingSlotRepository.findAllByDeletedFalse()
                .stream()
                .map(ParkingSlotMapper::mapToParkingSlotResponse)
                .toList();
    }

    @Override
    public List<ParkingSlotResponse> getSlotsByParkingArea(Long parkingAreaId) {
        ParkingArea parkingArea = getActiveParkingArea(parkingAreaId);
        validateParkingAreaOwner(parkingArea);

        return parkingSlotRepository
                .findByParkingAreaIdAndDeletedFalse(parkingAreaId)
                .stream()
                .map(ParkingSlotMapper::mapToParkingSlotResponse)
                .toList();
    }

    @Override
    public List<ParkingSlotResponse> getSlotsByStatus(SlotStatus slotStatus) {
        return parkingSlotRepository
                .findBySlotStatusAndDeletedFalse(slotStatus)
                .stream()
                .map(ParkingSlotMapper::mapToParkingSlotResponse)
                .toList();
    }

    @Override
    public List<ParkingSlotResponse> getSlotsByVehicleType(VehicleType vehicleType) {
        return parkingSlotRepository
                .findByVehicleTypeAndDeletedFalse(vehicleType)
                .stream()
                .map(ParkingSlotMapper::mapToParkingSlotResponse)
                .toList();
    }

    @Override
    public List<ParkingSlotResponse> getAvailableSlotsByArea(Long parkingAreaId) {
        ParkingArea parkingArea = getActiveParkingArea(parkingAreaId);
        validateParkingAreaOwner(parkingArea);

        return parkingSlotRepository
                .findByParkingAreaIdAndSlotStatusAndDeletedFalse(
                        parkingAreaId, SlotStatus.AVAILABLE)
                .stream()
                .map(ParkingSlotMapper::mapToParkingSlotResponse)
                .toList();
    }

    @Override
    public List<ParkingSlotResponse> getAvailableSlotsByAreaAndVehicleType(Long parkingAreaId, VehicleType vehicleType) {
        ParkingArea parkingArea = getActiveParkingArea(parkingAreaId);
        validateParkingAreaOwner(parkingArea);

        return parkingSlotRepository
                .findByParkingAreaIdAndVehicleTypeAndSlotStatusAndDeletedFalse(
                        parkingAreaId, vehicleType, SlotStatus.AVAILABLE)
                .stream()
                .map(ParkingSlotMapper::mapToParkingSlotResponse)
                .toList();
    }

    @Override
    public ParkingSlotResponse searchSlot(Long parkingAreaId,
                                          VehicleType vehicleType, String slotNumber) {


        ParkingSlot slot = parkingSlotRepository
                .findByParkingAreaIdAndVehicleTypeAndSlotNumberIgnoreCaseAndDeletedFalse(
                        parkingAreaId, vehicleType, slotNumber.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found"));
        validateParkingAreaOwner(slot.getParkingArea());
        return ParkingSlotMapper.mapToParkingSlotResponse(slot);
    }

    @Override
    public void deleteParkingSlot(Long id) {
        ParkingSlot parkingSlot = getActiveParkingSlot(id);
        validateParkingAreaOwner(parkingSlot.getParkingArea());
        if (parkingSlot.getDeleted()) {
            throw new BadRequestException("Parking slot is already deleted");
        }

        parkingSlot.setDeleted(true);
        parkingSlotRepository.save(parkingSlot);
    }

    @Override
    public ParkingSlotResponse restoreParkingSlot(Long id) {
        ParkingSlot parkingSlot = getParkingSlot(id);
        validateParkingAreaOwner(parkingSlot.getParkingArea());
        if (!parkingSlot.getDeleted()) {
            throw new BadRequestException("Parking slot is already active");
        }

        parkingSlot.setDeleted(false);
        ParkingSlot saved = parkingSlotRepository.save(parkingSlot);
        return ParkingSlotMapper.mapToParkingSlotResponse(saved);
    }

    private ParkingSlot getActiveParkingSlot(Long id) {
        return parkingSlotRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Parking slot not found with id : " + id));
    }

    private ParkingSlot getParkingSlot(Long id) {
        return parkingSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Parking slot not found with id : " + id));
    }

    private ParkingArea getActiveParkingArea(Long id) {
        return parkingAreaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Parking area not found with id : " + id));
    }

    private void createSlots(ParkingArea parkingArea,
            String prefix, Integer count,
            VehicleType vehicleType, Integer floorNumber) {

        for (int i = 1; i <= count; i++) {

            ParkingSlot parkingSlot = new ParkingSlot();

            parkingSlot.setSlotNumber(prefix + String.format("%03d", i));

            parkingSlot.setFloorNumber(floorNumber);
            parkingSlot.setVehicleType(vehicleType);
            parkingSlot.setSlotStatus(SlotStatus.AVAILABLE);
            parkingSlot.setParkingArea(parkingArea);

            parkingSlotRepository.save(parkingSlot);
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
        if (user.getRole().equals(Role.USER) ||
                user.getRole().equals(Role.SECURITY)) {
            return;
        }
        if (parkingArea.getOwner() == null ||
                !parkingArea.getOwner().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot access this parking area");
        }
    }

    @Override
    public ParkingSlotResponse updateSlotStatus(Long id, String status) {
        ParkingSlot parkingSlot = getActiveParkingSlot(id);
        validateParkingAreaOwner(parkingSlot.getParkingArea());

        try {
            SlotStatus newStatus = SlotStatus.valueOf(status.toUpperCase());
            parkingSlot.setSlotStatus(newStatus);
            ParkingSlot saved = parkingSlotRepository.save(parkingSlot);
            return ParkingSlotMapper.mapToParkingSlotResponse(saved);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid slot status: " + status +
                    ". Valid values: AVAILABLE, RESERVED, OCCUPIED, MAINTENANCE");
        }
    }
}


