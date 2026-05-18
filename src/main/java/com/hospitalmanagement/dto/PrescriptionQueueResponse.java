package com.hospitalmanagement.dto;

public record PrescriptionQueueResponse(
        Long prescriptionItemId,
        String prescriptionNumber,
        String patientName,
        String medicationName,
        String strength,
        String dosage,
        String frequency,
        Integer quantity,
        boolean dispensed
) {
}
