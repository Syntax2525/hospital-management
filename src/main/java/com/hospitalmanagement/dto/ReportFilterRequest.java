package com.hospitalmanagement.dto;

import java.time.LocalDate;

public record ReportFilterRequest(
        String range,
        LocalDate fromDate,
        LocalDate toDate,
        String department
) {
}
