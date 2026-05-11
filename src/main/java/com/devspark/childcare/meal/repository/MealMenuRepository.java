package com.devspark.childcare.meal.repository;

import com.devspark.childcare.meal.entity.MealMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealMenuRepository extends JpaRepository<MealMenu, UUID> {

    // Find a menu by its specific date
    Optional<MealMenu> findByDate(LocalDate date);
}