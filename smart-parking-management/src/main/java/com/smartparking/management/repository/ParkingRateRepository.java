package com.smartparking.management.repository;

import com.smartparking.management.entity.ParkingRate;
import com.smartparking.management.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingRateRepository extends JpaRepository<ParkingRate,Long> {


    boolean existsByParkingAreaIdAndVehicleTypeAndDeletedFalse(Long parkingAreaId, VehicleType vehicleType);
    Optional<ParkingRate> findByIdAndDeletedFalse(Long id);
    List<ParkingRate> findByParkingAreaIdAndDeletedFalse(Long parkingAreaId);
    List<ParkingRate> findAllByDeletedFalse();

    Optional<ParkingRate> findByParkingAreaIdAndVehicleTypeAndDeletedFalse(Long parkingAreaId, VehicleType vehicleType);
    Optional<ParkingRate> findByParkingAreaIdAndVehicleType(Long parkingAreaId, VehicleType vehicleType);
}
