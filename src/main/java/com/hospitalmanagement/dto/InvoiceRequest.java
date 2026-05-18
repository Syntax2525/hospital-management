package com.hospitalmanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record InvoiceRequest(
        @NotNull Long patientId,
        Long encounterId,
        @Valid @NotEmpty List<InvoiceLineItemDto> items
) {
}
