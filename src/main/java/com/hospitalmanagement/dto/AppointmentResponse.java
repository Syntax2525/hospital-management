package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.AppointmentStatus;
import com.hospitalmanagement.enums.AppointmentType;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Long assignedDoctorId,
        String assignedDoctorName,
        LocalDateTime scheduledAt,
        AppointmentType type,
        AppointmentStatus status,
        String reason
) {
}
