package com.devspark.childcare.meal.service;

import com.devspark.childcare.child.Child;
import com.devspark.childcare.child.ChildRepository;
import com.devspark.childcare.meal.entity.MealConsumptionLog;
import com.devspark.childcare.meal.repository.MealConsumptionLogRepository;
import com.devspark.childcare.meal.dto.MealLogRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealServiceImpl implements MealService {

    private final MealConsumptionLogRepository mealLogRepository;
    private final ChildRepository childRepository;

    @Override
    @Transactional // Architect Rule: Must use @Transactional when writing to DB
    public void saveMealLog(MealLogRequestDTO requestDTO) {

        // 1. Fetch the child from the database to ensure they exist
        Child child = childRepository.findById(requestDTO.childId())
                .orElseThrow(() -> new RuntimeException("Child not found"));

        // 2. Check if a log already exists for this child, date, and meal type
        Optional<MealConsumptionLog> existingLogOpt = mealLogRepository
                .findByChildIdAndDateAndMealType(
                        requestDTO.childId(),
                        requestDTO.date(),
                        requestDTO.mealType()
                );

        MealConsumptionLog logToSave;

        if (existingLogOpt.isPresent()) {
            // 3a. If it exists, update the status and note (Override feature for Teacher)
            logToSave = existingLogOpt.get();
            logToSave.setConsumptionStatus(requestDTO.consumptionStatus());
            logToSave.setNote(requestDTO.note());
        } else {
            // 3b. If it does not exist, create a new log
            logToSave = new MealConsumptionLog();
            logToSave.setChild(child);
            logToSave.setDate(requestDTO.date());
            logToSave.setMealType(requestDTO.mealType());
            logToSave.setConsumptionStatus(requestDTO.consumptionStatus());
            logToSave.setNote(requestDTO.note());

            // MAGIC: The 'createdBy' (Teacher ID) will be automatically
            // saved by Spring Security JWT and AuditableEntity.
            // We do not need to set it manually!
        }

        // 4. Save to the database
        mealLogRepository.save(logToSave);
    }

    @Override
    @Transactional
    public void saveBulkMealLogs(List<MealLogRequestDTO> requestDTOs) {
        // Loop through the list and save each log (Used for "Mark All As Full" button)
        for (MealLogRequestDTO dto : requestDTOs) {
            saveMealLog(dto);
        }
    }
}