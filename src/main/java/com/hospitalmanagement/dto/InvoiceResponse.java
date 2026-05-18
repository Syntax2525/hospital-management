package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponse(
        Long id,
        String invoiceNumber,
        String patientNumber,
        String patientName,
        LocalDateTime issuedAt,
        BigDecimal totalAmount,
        InvoiceStatus status,
        List<InvoiceLineItemDto> items
) {
}
