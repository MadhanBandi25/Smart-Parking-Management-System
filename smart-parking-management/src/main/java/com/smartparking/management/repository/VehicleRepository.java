package com.smartparking.management.repository;

import com.smartparking.management.entity.Vehicle;
import com.smartparking.management.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByVehicleNumberAndDeletedFalse(String vehicleNumber);
    Optional<Vehicle> findByIdAndDeletedFalse(Long id);

    List<Vehicle> findAllByDeletedFalse();
    List<Vehicle> findByVehicleNumberContainingIgnoreCaseAndDeletedFalse(String vehicleNumber);
    List<Vehicle> findByVehicleTypeAndDeletedFalse(VehicleType vehicleType);

    List<Vehicle> findByUserIdAndDeletedFalse(Long userId);

    long countByUserIdAndDeletedFalse(Long userId);
}
