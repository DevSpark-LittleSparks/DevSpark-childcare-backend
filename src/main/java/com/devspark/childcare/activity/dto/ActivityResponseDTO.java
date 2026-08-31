package com.devspark.childcare.activity.dto;

import com.devspark.childcare.activity.enums.ActivityCategory;
import java.util.UUID;

/**
 */
public record ActivityResponseDTO(
        UUID id,
        String name,
        ActivityCategory category, // මෙතන String තිබුණ එක ActivityCategory කළා
        String description,
        String materialsNeeded
) {}