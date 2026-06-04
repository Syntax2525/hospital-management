package com.hospitalmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PharmacyDispenseRequest(
        @NotNull @Positive Long prescriptionItemId,
        @Positive Long dispensedById,
        @Min(1) Integer quantityDispensed
) {
}
