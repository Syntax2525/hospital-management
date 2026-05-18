package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.BloodGroup;
import com.hospitalmanagement.enums.Gender;
import com.hospitalmanagement.enums.PatientPriority;
import com.hospitalmanagement.enums.PatientStatus;

import java.time.LocalDate;
import java.util.List;

public record PatientDetailResponse(
        Long id,
        String patientNumber,
        String fullName,
        Gender gender,
        Integer age,
        String phone,
        String address,
        String insuranceProvider,
        BloodGroup bloodGroup,
        PatientPriority priority,
        PatientStatus status,
        LocalDate lastVisitDate,
        String photoUrl,
        List<String> allergies
) {
}
