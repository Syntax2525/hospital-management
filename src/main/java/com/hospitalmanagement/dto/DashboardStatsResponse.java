package com.hospitalmanagement.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(
        long totalPatients,
        long appointmentsToday,
        long availableBeds,
        BigDecimal monthToDateRevenue,
        long pendingLabTests,
        long lowStockAlerts,
        long pendingInvites
) {
}
