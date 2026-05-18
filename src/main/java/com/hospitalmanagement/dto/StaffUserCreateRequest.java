package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StaffUserCreateRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String temporaryPassword,
        @NotNull UserRole role,
        Long departmentId,
        String phone
) {
}
