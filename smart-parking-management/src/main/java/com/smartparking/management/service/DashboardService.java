package com.smartparking.management.service;

import com.smartparking.management.dto.response.AdminDashboardResponse;
import com.smartparking.management.dto.response.ParkingOwnerDashboardResponse;
import com.smartparking.management.dto.response.UserDashboardResponse;

public interface DashboardService {

    AdminDashboardResponse getAdminDashboard();
    UserDashboardResponse getUserDashboard();
    ParkingOwnerDashboardResponse getParkingOwnerDashboard();
}
