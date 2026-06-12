package com.smartparking.management.controller;

import com.smartparking.management.dto.response.AdminDashboardResponse;
import com.smartparking.management.dto.response.ParkingOwnerDashboardResponse;
import com.smartparking.management.dto.response.UserDashboardResponse;
import com.smartparking.management.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/user")
    public ResponseEntity<UserDashboardResponse> getUserDashboard() {
        return ResponseEntity.ok(dashboardService.getUserDashboard());
    }

    @GetMapping("/owner")
    public ResponseEntity<ParkingOwnerDashboardResponse> getParkingOwnerDashboard() {
        return ResponseEntity.ok(dashboardService.getParkingOwnerDashboard());
    }
}
