package com.devspark.childcare.meal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealMenuRepository extends JpaRepository<MealMenu, UUID> {

    // Used to check if a menu exists for a specific date before saving/updating
    Optional<MealMenu> findByDateAndDeletedFalse(LocalDate date);

    // Used to fetch the menu data for a given week (Editing functionality)
    List<MealMenu> findByDateBetweenAndDeletedFalseOrderByDateAsc(LocalDate startDate, LocalDate endDate);
}