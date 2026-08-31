package com.devspark.childcare.meal;

import com.devspark.childcare.meal.dto.BulkConsumptionRequest;
import com.devspark.childcare.meal.dto.ConsumptionLogRequest;
import com.devspark.childcare.meal.dto.ConsumptionLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealConsumptionService {

    private final MealConsumptionLogRepository repository;

    @Transactional // Ensures atomicity for bulk operations[cite: 5]
    public void saveBulkLogs(BulkConsumptionRequest request) {
        List<MealConsumptionLog> entitiesToSave = new ArrayList<>();

        for (ConsumptionLogRequest logReq : request.logs()) {
            // Check if log already exists to support updates (Overriding)
            MealConsumptionLog existingLog = repository
                    .findByChildIdAndMenuIdAndMealTypeAndDeletedFalse(
                            logReq.childId(), logReq.menuId(), logReq.mealType())
                    .orElse(null);

            if (existingLog != null) {
                // Update existing record
                existingLog.setConsumptionStatus(logReq.consumptionStatus());
                existingLog.setNote(logReq.note());
                entitiesToSave.add(existingLog);
            } else {
                // Create new record
                MealConsumptionLog newLog = MealConsumptionLog.builder()
                        .childId(logReq.childId())
                        .menuId(logReq.menuId())
                        .mealType(logReq.mealType())
                        .consumptionStatus(logReq.consumptionStatus())
                        .note(logReq.note())
                        .date(logReq.date())
                        .deleted(false)
                        .build();
                entitiesToSave.add(newLog);
            }
        }
        repository.saveAll(entitiesToSave);
    }

    /**
     * Fetches existing logs for a specific date. Required for Teacher Editing UI.
     */
    public List<ConsumptionLogResponse> getLogsForDate(LocalDate date) {
        return repository.findByDateAndDeletedFalse(date).stream()
                .map(log -> new ConsumptionLogResponse(
                        log.getChildId(),
                        log.getMenuId(),
                        log.getMealType(),
                        log.getConsumptionStatus(),
                        log.getNote()
                ))
                .toList();
    }
}