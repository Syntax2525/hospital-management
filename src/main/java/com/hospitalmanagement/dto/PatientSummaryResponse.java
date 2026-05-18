package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.Gender;
import com.hospitalmanagement.enums.PatientPriority;

public record PatientSummaryResponse(
        Long id,
        String patientNumber,
        String fullName,
        Gender gender,
        Integer age,
        String phone,
        PatientPriority priority
) {
}
