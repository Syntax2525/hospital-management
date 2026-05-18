package com.hospitalmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PharmacyDispenseRequest(
        @NotNull Long prescriptionItemId,
        Long dispensedById,
        @Min(1) Integer quantityDispensed
) {
}
