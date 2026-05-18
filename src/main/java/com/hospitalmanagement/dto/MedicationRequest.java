package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.InventoryStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record MedicationRequest(
        @NotBlank String name,
        String strength,
        String dosageForm,
        @Min(0) Integer stockQuantity,
        @Min(0) Integer reorderLevel,
        @DecimalMin("0.00") BigDecimal unitPrice,
        InventoryStatus inventoryStatus
) {
}
