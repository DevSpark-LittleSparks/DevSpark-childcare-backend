package com.devspark.childcare.meal;

import com.devspark.childcare.meal.dto.MealMenuCreateRequest;
import com.devspark.childcare.meal.dto.MealMenuResponse;
import com.devspark.childcare.meal.dto.WeeklyMenuRequest;
import com.devspark.childcare.meal.mapper.MealMenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation containing the core business logic for Meal Menus[cite: 4].
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MealMenuServiceImpl implements MealMenuService {

    private final MealMenuRepository mealMenuRepository;
    private final MealMenuMapper mealMenuMapper;

    @Override
    @Transactional // Ensures atomicity for batch writes. Rolls back entirely if any save fails[cite: 4].
    public List<MealMenuResponse> publishWeeklyMenu(WeeklyMenuRequest request) {
        List<MealMenu> savedMenus = new ArrayList<>();

        for (MealMenuCreateRequest dailyMenuRequest : request.menus()) {
            // Retrieve existing record to support update (edit) operations
            MealMenu existingMenu = mealMenuRepository.findByDateAndDeletedFalse(dailyMenuRequest.date())
                    .orElse(null);

            if (existingMenu != null) {
                // Update existing record
                mealMenuMapper.updateEntityFromDto(dailyMenuRequest, existingMenu);
                savedMenus.add(mealMenuRepository.save(existingMenu));
            } else {
                // Insert new record
                MealMenu newMenu = mealMenuMapper.toEntity(dailyMenuRequest);
                savedMenus.add(mealMenuRepository.save(newMenu));
            }
        }

        log.info("Successfully processed and saved weekly menu for {} days", savedMenus.size());
        return mealMenuMapper.toResponseList(savedMenus);
    }

    @Override
    @Transactional(readOnly = true) // Performance optimization for read operations[cite: 4]
    public List<MealMenuResponse> getMenuForDateRange(LocalDate startDate, LocalDate endDate) {
        List<MealMenu> menus = mealMenuRepository.findByDateBetweenAndDeletedFalseOrderByDateAsc(startDate, endDate);
        return mealMenuMapper.toResponseList(menus);
    }
}