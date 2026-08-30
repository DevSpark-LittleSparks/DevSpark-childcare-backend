package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.ActivityRequestDTO;
import com.devspark.childcare.activity.dto.ActivityResponseDTO;
import java.util.List;
import java.util.UUID;

public interface ActivityService {
    ActivityResponseDTO createActivity(ActivityRequestDTO requestDTO);

    // Method to fetch all activities for the admin dropdown
    List<ActivityResponseDTO> getAllActivities();

    // 👇 Edit සහ Delete සඳහා අලුතින් එකතු කළා
    ActivityResponseDTO updateActivity(UUID id, ActivityRequestDTO requestDTO);
    void deleteActivity(UUID id);
}