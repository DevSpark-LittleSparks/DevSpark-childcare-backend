package com.devspark.childcare.meal.service;

import com.devspark.childcare.meal.entity.MealMenu;
import com.devspark.childcare.meal.repository.MealMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j // Used for logging messages to the console
@Service
@RequiredArgsConstructor
public class MealNotificationService {

    private final MealMenuRepository mealMenuRepository;

    // This cron expression means: Run at 18:00:00 (6:00 PM) every day.
    // Format is: "Seconds Minutes Hours Day-of-month Month Day-of-week"
    @Scheduled(cron = "0 0 18 * * ?")
    @Transactional(readOnly = true)
    public void sendEveningMealReminder() {

        log.info("Executing 6 PM Cron Job: Checking tomorrow's meal menu...");

        // 1. Get tomorrow's date
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // 2. Fetch tomorrow's menu from the database
        Optional<MealMenu> tomorrowMenuOpt = mealMenuRepository.findByDate(tomorrow);

        if (tomorrowMenuOpt.isPresent()) {
            MealMenu menu = tomorrowMenuOpt.get();

            // 3. Logic to send notifications to parents
            // Note: Since the actual SMS/Email/Push notification service might
            // be handled by another module, we simulate it here with logs for now.

            log.info("Tomorrow's menu found! Breakfast: {}, Lunch: {}",
                    menu.getBreakfastDetails(), menu.getLunchDetails());

            // TODO: Call the actual NotificationService here to send to parents
            log.info("Simulating: Sending push notifications to all parents...");

        } else {
            // If the Admin hasn't added a menu for tomorrow, do nothing
            log.warn("No menu found for tomorrow ({}). No notifications sent.", tomorrow);
        }
    }
}