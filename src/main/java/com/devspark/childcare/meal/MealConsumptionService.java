package com.devspark.childcare.meal;

import com.devspark.childcare.meal.dto.BulkConsumptionRequest;
import com.devspark.childcare.meal.dto.ConsumptionLogRequest;
import com.devspark.childcare.meal.dto.ConsumptionLogResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
    private final EntityManager entityManager;

    // Frontend එකට යවන ළමයින්ගේ ඩේටා ෆෝමැට් එක (DTO)
    public record MealStudentDto(String id, String fullName, String stage, String carePlan) {
    }

    @Transactional
    public void saveBulkLogs(BulkConsumptionRequest request) {
        List<MealConsumptionLog> entitiesToSave = new ArrayList<>();

        for (ConsumptionLogRequest logReq : request.logs()) {
            MealConsumptionLog existingLog = repository
                    .findByChildIdAndMenuIdAndMealTypeAndDeletedFalse(
                            logReq.childId(), logReq.menuId(), logReq.mealType())
                    .orElse(null);

            if (existingLog != null) {
                existingLog.setConsumptionStatus(logReq.consumptionStatus());
                existingLog.setNote(logReq.note());
                entitiesToSave.add(existingLog);
            } else {
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

    @SuppressWarnings("unchecked")
    public List<Object> getPresentStudentsForMeals(LocalDate date) {

        // CAST වෙනුවට MySQL 8 හි නිල BIN_TO_UUID ෆන්ක්ෂන් එක භාවිතා කර ඇත
        String sql = "SELECT BIN_TO_UUID(c.child_id), CONCAT(c.first_name, ' ', c.last_name), 'N/A', c.special_note " +
                "FROM child c " +
                "INNER JOIN attendance a ON c.child_id = a.child_id " +
                "WHERE a.date = :date AND a.status = 'PRESENT' AND c.deleted = false";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("date", date);

        List<Object[]> rows = (List<Object[]>) query.getResultList();
        List<Object> students = new ArrayList<>();

        for (Object[] row : rows) {
            students.add(new MealStudentDto(
                    row[0] != null ? row[0].toString() : null, // දැන් හරියටම UUID String එක මෙතනට එනවා
                    row[1] != null ? row[1].toString() : "Unknown",
                    (String) row[2],
                    row[3] != null ? row[3].toString() : ""
            ));
        }

        return students;
    }
}