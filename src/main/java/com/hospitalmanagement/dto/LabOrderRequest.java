package com.hospitalmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LabOrderRequest(
        @NotNull @Positive Long patientId,
        @Positive Long consultationId,
        @Positive Long requestedById,
        @Size(max = 1000)
        String notes,
        @NotEmpty @Size(max = 50) List<@NotBlank @Size(max = 120) String> tests
) {
}
