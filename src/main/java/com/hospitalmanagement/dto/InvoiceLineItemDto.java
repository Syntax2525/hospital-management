package com.hospitalmanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record InvoiceLineItemDto(
        @NotBlank @Size(max = 160) String label,
        @NotNull @Min(1) Integer quantity,
        @NotNull @DecimalMin("0.01") BigDecimal unitAmount,
        @DecimalMin("0.00") BigDecimal lineTotal
) {
}
