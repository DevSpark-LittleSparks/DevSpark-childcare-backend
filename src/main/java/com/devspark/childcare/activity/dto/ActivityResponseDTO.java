package com.devspark.childcare.activity.dto;

import java.util.UUID;

/**
 * DTO for sending Activity data back to the Frontend.
 * We never expose the raw Database Entity to the client (README Rule).
 */
public record ActivityResponseDTO(
        UUID id,
        String name,
        String category,
        String description,
        String materialsNeeded
) {}