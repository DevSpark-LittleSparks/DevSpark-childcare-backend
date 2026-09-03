package com.devspark.childcare.meal;

import com.devspark.childcare.meal.dto.MealMenuResponse;
import com.devspark.childcare.meal.dto.WeeklyMenuRequest;
import com.devspark.childcare.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meals/menu")
@RequiredArgsConstructor
public class MealMenuController {

    private final MealMenuService mealMenuService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN')")
    public ApiResponse<List<MealMenuResponse>> publishWeeklyMenu(
            @Valid @RequestBody WeeklyMenuRequest request) {

        List<MealMenuResponse> responses = mealMenuService.publishWeeklyMenu(request);
        return ApiResponse.<List<MealMenuResponse>>success("Weekly menu published successfully", responses);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'STAFF', 'ROLE_STAFF', 'TEACHER', 'ROLE_TEACHER', 'GUARDIAN', 'ROLE_GUARDIAN', 'PARENT', 'ROLE_PARENT')")
    public ApiResponse<List<MealMenuResponse>> getWeeklyMenu(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<MealMenuResponse> responses = mealMenuService.getMenuForDateRange(startDate, endDate);
        return ApiResponse.<List<MealMenuResponse>>success("Menu fetched successfully", responses);
    }
}