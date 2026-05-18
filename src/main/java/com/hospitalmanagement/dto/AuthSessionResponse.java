package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.UserRole;

public record AuthSessionResponse(
        boolean loggedIn,
        String displayName,
        UserRole role,
        String email,
        String token
) {
}
