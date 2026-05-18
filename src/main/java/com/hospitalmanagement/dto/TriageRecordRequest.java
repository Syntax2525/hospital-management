package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.PatientPriority;
import com.hospitalmanagement.enums.TriageCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TriageRecordRequest(
        @NotNull Long patientId,
        Long encounterId,
        Long nurseId,
        @NotBlank String chiefComplaint,
        TriageCategory category,
        PatientPriority priority,
        @Valid VitalSignDto vitalSign
) {
}
