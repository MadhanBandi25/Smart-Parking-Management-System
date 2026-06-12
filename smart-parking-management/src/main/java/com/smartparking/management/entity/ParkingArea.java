package com.smartparking.management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "parking_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,  length = 100)
    private String areaName;

    @Column(nullable = false, length = 150)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Integer bikeSlots;

    @Column(nullable = false)
    private Integer evBikeSlots;

    @Column(nullable = false)
    private Integer carSlots;

    @Column(nullable = false)
    private Integer evCarSlots;

    @Column(nullable = false)
    private Integer truckSlots;

    @Column(nullable = false)
    private Integer totalSlots;

    @Column(nullable = false)
    private Integer availableSlots;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    private Boolean deleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "parkingArea", cascade = CascadeType.ALL)
    private List<ParkingSlot> parkingSlots;
}
