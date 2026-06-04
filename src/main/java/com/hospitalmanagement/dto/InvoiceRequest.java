package com.hospitalmanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record InvoiceRequest(
        @NotNull @Positive Long patientId,
        @Positive Long encounterId,
        @Valid @NotEmpty List<InvoiceLineItemDto> items
) {
}
