package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.PatientPriority;
import com.hospitalmanagement.enums.TriageCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TriageRecordRequest(
        @NotNull @Positive Long patientId,
        @Positive Long encounterId,
        @Positive Long nurseId,
        @NotBlank @Size(max = 1000) String chiefComplaint,
        TriageCategory category,
        PatientPriority priority,
        @Valid VitalSignDto vitalSign
) {
}
