package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.AccountStatus;
import com.hospitalmanagement.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StaffUserUpdateRequest(
        @NotBlank @Size(max = 160) String fullName,
        @Email @NotBlank String email,
        @NotNull UserRole role,
        @NotNull AccountStatus status,
        @Positive Long departmentId,
        @Pattern(regexp = "^$|^[+0-9()\\-\\s]{7,30}$", message = "must be a valid phone number")
        String phone,
        @Size(min = 8, max = 100) String newPassword
) {
}
