package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.LabOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record LabOrderResponse(
        Long id,
        String orderNumber,
        Long patientId,
        String patientName,
        LocalDateTime requestedAt,
        LabOrderStatus status,
        List<String> tests
) {
}
