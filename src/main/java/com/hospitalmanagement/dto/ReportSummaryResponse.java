package com.hospitalmanagement.dto;

import java.math.BigDecimal;

public record ReportSummaryResponse(
        long totalPatients,
        long appointments,
        long labOrders,
        long invoices,
        long availableBeds,
        long occupiedBeds,
        long lowStockMedications,
        BigDecimal revenue
) {
}
