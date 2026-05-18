package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.AppointmentType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull Long patientId,
        Long assignedDoctorId,
        @NotNull LocalDateTime scheduledAt,
        AppointmentType type,
        String reason
) {
}
