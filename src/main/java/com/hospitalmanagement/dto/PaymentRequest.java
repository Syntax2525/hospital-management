package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull @Positive Long invoiceId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull PaymentMethod method
) {
}
