package com.hospitalmanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ConsultationRequest(
        @NotNull @Positive Long patientId,
        @Positive Long encounterId,
        @Positive Long doctorId,
        @Size(max = 4000)
        String clinicalNotes,
        @Size(max = 4000)
        String treatmentPlan,
        @Size(max = 20) List<@NotBlank @Size(max = 40) String> diagnosisCodes,
        @Size(max = 50) List<@NotBlank @Size(max = 120) String> requestedLabTests,
        @Size(max = 50) List<@Valid PrescriptionItemDto> prescriptionItems
) {
}
