package com.smartparking.management.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RevenueReportResponse {

    private LocalDate fromDate;
    private LocalDate toDate;

    private BigDecimal totalRevenue;
    private Long totalPayments;
}
