package com.smartparking.management.repository;

import com.smartparking.management.entity.ParkingArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingAreaRepository extends JpaRepository<ParkingArea, Long> {

    boolean existsByAreaNameIgnoreCaseAndAddressIgnoreCaseAndDeletedFalse(String areaName, String address);
    Optional<ParkingArea> findByIdAndDeletedFalse(Long id);

    List<ParkingArea> findAllByDeletedFalse();
    List<ParkingArea> findByAddressContainingIgnoreCaseAndDeletedFalse(String address);
    List<ParkingArea> findByCityContainingIgnoreCaseAndDeletedFalse(String city);

    long countByDeletedFalse();

    List<ParkingArea> findByOwnerIdAndDeletedFalse(Long ownerId);
    Optional<ParkingArea> findByIdAndOwnerIdAndDeletedFalse(Long id, Long ownerId);
    long countByOwnerIdAndDeletedFalse(Long ownerId);
}
