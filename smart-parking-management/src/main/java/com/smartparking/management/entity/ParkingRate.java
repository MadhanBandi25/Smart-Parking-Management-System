package com.smartparking.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smartparking.management.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_rates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_area_vehicle_type",
                        columnNames = {
                                "parking_area_id",
                                "vehicle_type"
                        }
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType vehicleType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weekdayRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weekendRate;

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
}
