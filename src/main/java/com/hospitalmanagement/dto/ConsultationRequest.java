package com.hospitalmanagement.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ConsultationRequest(
        @NotNull Long patientId,
        Long encounterId,
        Long doctorId,
        String clinicalNotes,
        String treatmentPlan,
        List<String> diagnosisCodes,
        List<String> requestedLabTests,
        List<PrescriptionItemDto> prescriptionItems
) {
}
