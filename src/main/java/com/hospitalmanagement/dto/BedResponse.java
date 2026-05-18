package com.hospitalmanagement.dto;

import com.hospitalmanagement.enums.BedStatus;

public record BedResponse(
        Long id,
        String bedNumber,
        BedStatus status,
        Long wardId,
        String wardName,
        String wardFloor
) {
}
