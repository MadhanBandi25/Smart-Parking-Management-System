package com.smartparking.management.repository;

import com.smartparking.management.entity.ParkingSlot;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.enums.VehicleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot,Long> {

    boolean existsByParkingAreaIdAndDeletedFalse(Long parkingAreaId);
    Optional<ParkingSlot> findByIdAndDeletedFalse(Long id);
    List<ParkingSlot> findAllByDeletedFalse();

    List<ParkingSlot> findByParkingAreaIdAndDeletedFalse(Long parkingAreaId);
    List<ParkingSlot> findBySlotStatusAndDeletedFalse(SlotStatus slotStatus);

    List<ParkingSlot> findByVehicleTypeAndDeletedFalse(VehicleType vehicleType);
    List<ParkingSlot> findByParkingAreaIdAndSlotStatusAndDeletedFalse(Long parkingAreaId, SlotStatus slotStatus);
    List<ParkingSlot> findByParkingAreaIdAndVehicleTypeAndSlotStatusAndDeletedFalse(
            Long parkingAreaId, VehicleType vehicleType, SlotStatus slotStatus);

    Optional<ParkingSlot> findByParkingAreaIdAndVehicleTypeAndSlotNumberIgnoreCaseAndDeletedFalse(
            Long parkingAreaId, VehicleType vehicleType, String slotNumber);


    List<ParkingSlot> findByParkingAreaIdAndVehicleTypeAndDeletedFalse(Long parkingAreaId, VehicleType vehicleType);
    List<ParkingSlot> findByParkingAreaIdAndVehicleType(Long parkingAreaId, VehicleType vehicleType);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT ps
        FROM ParkingSlot ps
        WHERE ps.id = :slotId
        AND ps.deleted = false
        """)
    Optional<ParkingSlot> findByIdForUpdate(@Param("slotId") Long slotId);

    long countByDeletedFalse();
    long countBySlotStatusAndDeletedFalse(SlotStatus slotStatus);

    List<ParkingSlot> findByParkingAreaOwnerIdAndDeletedFalse(Long ownerId);
    long countByParkingAreaOwnerIdAndDeletedFalse(Long ownerId);
    long countByParkingAreaOwnerIdAndSlotStatusAndDeletedFalse(Long ownerId, SlotStatus slotStatus);


}
