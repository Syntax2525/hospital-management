package com.hospitalmanagement.dto;

public record WardResponse(
        Long id,
        String name,
        String floor,
        long totalBeds,
        long availableBeds
) {
}
