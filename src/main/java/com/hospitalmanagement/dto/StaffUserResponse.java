package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.AccountStatus;
import com.hospitalmanagement.enums.UserRole;

public record StaffUserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        AccountStatus status,
        String departmentName,
        String phone
) {
}
