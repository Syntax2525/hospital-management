package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull Long invoiceId,
        @DecimalMin("0.00") BigDecimal amount,
        @NotNull PaymentMethod method
) {
}
