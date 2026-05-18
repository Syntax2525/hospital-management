package com.hospitalmanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record InvoiceLineItemDto(
        @NotBlank String label,
        @Min(1) Integer quantity,
        @DecimalMin("0.00") BigDecimal unitAmount,
        @DecimalMin("0.00") BigDecimal lineTotal
) {
}
