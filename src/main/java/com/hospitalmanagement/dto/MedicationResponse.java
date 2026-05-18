package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.InventoryStatus;

import java.math.BigDecimal;

public record MedicationResponse(
        Long id,
        String name,
        String strength,
        String dosageForm,
        Integer stockQuantity,
        Integer reorderLevel,
        BigDecimal unitPrice,
        InventoryStatus inventoryStatus
) {
}
