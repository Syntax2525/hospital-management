package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.NotificationStatus;
import com.hospitalmanagement.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        NotificationType type,
        NotificationStatus status,
        LocalDateTime createdAt
) {
}
