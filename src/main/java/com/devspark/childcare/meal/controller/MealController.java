package com.devspark.childcare.meal.controller;

import com.devspark.childcare.meal.dto.MealLogRequestDTO;
import com.devspark.childcare.meal.service.MealService;
import com.devspark.childcare.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meals") // Architect Rule: API path standard
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    // Endpoint to save a single student's meal log
    @PostMapping("/log")
    public ResponseEntity<ApiResponse<String>> saveMealLog(@Valid @RequestBody MealLogRequestDTO requestDTO) {
        mealService.saveMealLog(requestDTO);

        // Architect Rule: Use ApiResponse wrapper
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Meal log saved successfully",
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint to bulk save meal logs (e.g., when Teacher clicks "Mark All As Full")
    @PostMapping("/log/bulk")
    public ResponseEntity<ApiResponse<String>> saveBulkMealLogs(@Valid @RequestBody List<MealLogRequestDTO> requestDTOs) {
        mealService.saveBulkMealLogs(requestDTOs);

        // Architect Rule: Use ApiResponse wrapper
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Bulk meal logs saved successfully",
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}