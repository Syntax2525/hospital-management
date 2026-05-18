package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PatientRegistrationRequest(
        @NotBlank String fullName,
        Gender gender,
        @Min(0) @Max(130) Integer age,
        String phone,
        String address,
        String insuranceProvider,
        String allergies
) {
}
