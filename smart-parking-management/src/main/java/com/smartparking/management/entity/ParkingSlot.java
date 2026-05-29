package com.smartparking.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartparking.management.enums.SlotStatus;
import com.smartparking.management.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "parking_slots",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_slot_area",
                        columnNames = {"slot_number", "parking_area_id"}
                )
     }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number", nullable = false, length = 30)
    private String slotNumber;

    @Column(nullable = false)
    private Integer floorNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SlotStatus slotStatus = SlotStatus.AVAILABLE;


    @Column(nullable = false)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_area_id", nullable = false)
    private ParkingArea parkingArea;

    @JsonIgnore
    @OneToMany(mappedBy = "parkingSlot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
