package com.smartparking.management.controller;

import com.smartparking.management.dto.response.BookingReportResponse;
import com.smartparking.management.dto.response.PaymentReportResponse;
import com.smartparking.management.dto.response.RevenueReportResponse;
import com.smartparking.management.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportResponse> getRevenueReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return ResponseEntity.ok(reportService.getRevenueReport(fromDate, toDate));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingReportResponse>> getBookingReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return ResponseEntity.ok(reportService.getBookingReport(fromDate, toDate));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentReportResponse>> getPaymentReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate) {

        return ResponseEntity.ok(reportService.getPaymentReport(fromDate, toDate));
    }
}
