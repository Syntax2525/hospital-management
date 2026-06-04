package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.InventoryStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MedicationRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 80)
        String strength,
        @Size(max = 80)
        String dosageForm,
        @Min(0) Integer stockQuantity,
        @Min(0) Integer reorderLevel,
        @DecimalMin("0.00") BigDecimal unitPrice,
        InventoryStatus inventoryStatus
) {
}
