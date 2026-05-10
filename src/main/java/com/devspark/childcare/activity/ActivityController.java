package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.ActivityRequestDTO;
import com.devspark.childcare.activity.dto.ActivityResponseDTO;
import com.devspark.childcare.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ActivityResponseDTO> createActivity(@Valid @RequestBody ActivityRequestDTO request) {
        ActivityResponseDTO response = activityService.createActivity(request);
        return ApiResponse.success("Activity created successfully", response);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<ActivityResponseDTO>> getAllActivities() {
        return ApiResponse.success("Activities fetched successfully", activityService.getAllActivities());
    }
}