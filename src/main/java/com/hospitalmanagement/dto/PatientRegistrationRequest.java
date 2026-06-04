package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientRegistrationRequest(
        @NotBlank @Size(max = 160) String fullName,
        Gender gender,
        @Min(0) @Max(130) Integer age,
        @Pattern(regexp = "^$|^[+0-9()\\-\\s]{7,30}$", message = "must be a valid phone number")
        String phone,
        @Size(max = 500)
        String address,
        @Size(max = 160)
        String insuranceProvider,
        @Size(max = 500)
        String allergies
) {
}
