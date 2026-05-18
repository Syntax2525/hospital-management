package com.hospitalmanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record LabOrderRequest(
        @NotNull Long patientId,
        Long consultationId,
        Long requestedById,
        String notes,
        @NotEmpty List<String> tests
) {
}
