package com.devspark.childcare.meal.service;

import com.devspark.childcare.meal.dto.MealLogRequestDTO;
import java.util.List;

public interface MealService {

    // Save a single meal log or update if it already exists
    void saveMealLog(MealLogRequestDTO requestDTO);

    // Bulk save meal logs (Used when Teacher marks all as FULL)
    void saveBulkMealLogs(List<MealLogRequestDTO> requestDTOs);
}