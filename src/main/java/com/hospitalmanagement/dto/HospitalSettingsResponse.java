package com.hospitalmanagement.dto;

import java.util.List;

public record HospitalSettingsResponse(
        String hospitalName,
        String accessPolicy,
        List<String> roles
) {
}
