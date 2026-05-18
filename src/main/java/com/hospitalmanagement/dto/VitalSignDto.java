package com.hospitalmanagement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record VitalSignDto(
        String bloodPressure,
        @DecimalMin("25.0") @DecimalMax("45.0") BigDecimal temperatureCelsius,
        @Min(0) @Max(250) Integer pulseBpm,
        @Min(0) @Max(100) Integer oxygenSaturation,
        @DecimalMin("0.0") BigDecimal weightKg,
        @DecimalMin("0.0") BigDecimal heightCm
) {
}
