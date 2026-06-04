package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.AppointmentType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull @Positive Long patientId,
        @Positive Long assignedDoctorId,
        @NotNull @FutureOrPresent LocalDateTime scheduledAt,
        AppointmentType type,
        @Size(max = 500)
        String reason
) {
}
