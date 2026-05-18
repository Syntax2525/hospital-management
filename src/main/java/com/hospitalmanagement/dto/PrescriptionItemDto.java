package com.hospitalmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PrescriptionItemDto(
        Long medicationId,
        @NotBlank String medicationName,
        @NotBlank String dosage,
        @NotBlank String frequency,
        String duration,
        @Min(1) Integer quantity
) {
}
