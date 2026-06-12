package com.smartparking.management.service;

import com.smartparking.management.dto.response.BookingReportResponse;
import com.smartparking.management.dto.response.PaymentReportResponse;
import com.smartparking.management.dto.response.RevenueReportResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    RevenueReportResponse getRevenueReport(LocalDate fromDate, LocalDate toDate);
    List<BookingReportResponse> getBookingReport(LocalDate fromDate, LocalDate toDate);
    List<PaymentReportResponse> getPaymentReport(LocalDate fromDate, LocalDate toDate);
}
