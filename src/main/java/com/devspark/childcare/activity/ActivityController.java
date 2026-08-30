package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.ActivityRequestDTO;
import com.devspark.childcare.activity.dto.ActivityResponseDTO;
import com.devspark.childcare.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    // 👇 Edit Master Activity Endpoint
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ActivityResponseDTO> updateActivity(@PathVariable("id") UUID id, @Valid @RequestBody ActivityRequestDTO request) {
        ActivityResponseDTO response = activityService.updateActivity(id, request);
        return ApiResponse.success("Activity updated successfully", response);
    }

    // 👇 Delete Master Activity Endpoint
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteActivity(@PathVariable("id") UUID id) {
        activityService.deleteActivity(id);
        return ApiResponse.success("Activity deleted successfully", null);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<ActivityResponseDTO>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<ActivityResponseDTO> list = activityService.getAllActivities();
        Page<ActivityResponseDTO> pagedResult = new PageImpl<>(list, PageRequest.of(page, size), list.size());
        return ApiResponse.success("Activities fetched successfully", pagedResult);
    }
}