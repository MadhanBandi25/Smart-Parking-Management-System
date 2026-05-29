package com.smartparking.management.service.impl;

import com.smartparking.management.dto.request.VehicleRequest;
import com.smartparking.management.dto.response.VehicleResponse;
import com.smartparking.management.entity.User;
import com.smartparking.management.entity.Vehicle;
import com.smartparking.management.enums.VehicleType;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.VehicleMapper;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.repository.VehicleRepository;
import com.smartparking.management.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public VehicleResponse addVehicle(VehicleRequest request) {

        String vehicleNumber =  normalizeVehicleNumber(request.getVehicleNumber());

        if (vehicleRepository.existsByVehicleNumberAndDeletedFalse(vehicleNumber)) {
            throw new BadRequestException("Vehicle number already exists");
        }

        User user = getLoggedInUser();
        Vehicle vehicle = VehicleMapper.mapToVehicleEntity(request);
        vehicle.setVehicleNumber(vehicleNumber);
        vehicle.setUser(user);
        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleMapper.mapToVehicleResponse(saved);
    }

    @Override
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = getActiveVehicle(id);
        return VehicleMapper.mapToVehicleResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAllByDeletedFalse()
                .stream()
                .map(VehicleMapper::mapToVehicleResponse)
                .toList();
    }

    @Override
    public List<VehicleResponse> getMyVehicles() {
        User user = getLoggedInUser();
        return vehicleRepository.findByUserIdAndDeletedFalse(user.getId())
                .stream()
                .map(VehicleMapper::mapToVehicleResponse)
                .toList();
    }

    @Override
    public List<VehicleResponse> searchByVehicleNumber(String vehicleNumber) {
        String normalizedVehicleNumber = normalizeVehicleNumber(vehicleNumber);
        return vehicleRepository
                .findByVehicleNumberContainingIgnoreCaseAndDeletedFalse(normalizedVehicleNumber)
                .stream()
                .map(VehicleMapper::mapToVehicleResponse)
                .toList();
    }

    @Override
    public List<VehicleResponse> filterByVehicleType(VehicleType vehicleType) {
        return vehicleRepository.findByVehicleTypeAndDeletedFalse(vehicleType)
                .stream()
                .map(VehicleMapper::mapToVehicleResponse)
                .toList();
    }

    @Override
    public void deleteVehicle(Long id) {
        Vehicle vehicle = getActiveVehicle(id);
        validateVehicleOwner(vehicle);
        if (vehicle.getDeleted()) {
            throw new BadRequestException("Vehicle is already deleted");
        }
        vehicle.setDeleted(true);
        vehicleRepository.save(vehicle);
    }

    @Override
    public VehicleResponse restoreVehicle(Long id) {
        Vehicle vehicle = getVehicle(id);
        validateVehicleOwner(vehicle);
        if (!vehicle.getDeleted()) {
            throw new BadRequestException("Vehicle is already active");
        }
        vehicle.setDeleted(false);
        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleMapper.mapToVehicleResponse(saved);
    }

    private Vehicle getActiveVehicle(Long id){
        return vehicleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vehicle not found with id : " + id));
    }
    private Vehicle getVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id : " + id));
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));
    }

    private void validateVehicleOwner(Vehicle vehicle) {
        User user = getLoggedInUser();
        if (!vehicle.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot access this vehicle");
        }
    }

    private String normalizeVehicleNumber(String vehicleNumber) {
        return vehicleNumber.trim().toUpperCase();
    }
}
