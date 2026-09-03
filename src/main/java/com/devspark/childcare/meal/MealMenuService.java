package com.devspark.childcare.meal;

import com.devspark.childcare.meal.dto.MealMenuResponse;
import com.devspark.childcare.meal.dto.WeeklyMenuRequest;

import java.time.LocalDate;
import java.util.List;

public interface MealMenuService {
    List<MealMenuResponse> publishWeeklyMenu(WeeklyMenuRequest request);
    List<MealMenuResponse> getMenuForDateRange(LocalDate startDate, LocalDate endDate);
}