package com.hospitalmanagement.dto;

import com.hospitalmanagement.validation.ValidPrescriptionItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@ValidPrescriptionItem
public record PrescriptionItemDto(
        @Positive Long medicationId,
        @Size(max = 160) String medicationName,
        @NotBlank @Size(max = 80) String dosage,
        @NotBlank @Size(max = 80) String frequency,
        @Size(max = 80)
        String duration,
        @NotNull @Min(1) Integer quantity
) {
}
