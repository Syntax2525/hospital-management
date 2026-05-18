package com.hospitalmanagement.dto;

import java.time.LocalDateTime;

public record LabResultResponse(
        Long id,
        String testName,
        String resultValues,
        String interpretation,
        LocalDateTime publishedAt
) {
}
