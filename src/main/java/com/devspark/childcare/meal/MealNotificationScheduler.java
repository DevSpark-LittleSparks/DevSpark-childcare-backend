package com.devspark.childcare.meal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Scheduled service to handle automated daily meal reminders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MealNotificationScheduler {

    private final MealMenuRepository mealMenuRepository;

    /**
     * Executes daily at 18:00 (6:00 PM) Sri Lanka time.
     * Scans for tomorrow's menu and triggers the notification process.
     */
    @Scheduled(cron = "0 0 18 * * ?", zone = "Asia/Colombo")
    public void sendEveningMealReminder() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Optional<MealMenu> tomorrowMenu = mealMenuRepository.findByDateAndDeletedFalse(tomorrow);

        if (tomorrowMenu.isPresent()) {
            log.info("Cron execution [18:00]: Menu found for tomorrow ({}). Processing notification...", tomorrow);

            MealMenu menu = tomorrowMenu.get();
            String message = String.format(
                    "Tomorrow's Menu -> Breakfast: %s | Lunch: %s | Evening Snack: %s",
                    menu.getBreakfastDetails() != null ? menu.getBreakfastDetails() : "Not specified",
                    menu.getLunchDetails() != null ? menu.getLunchDetails() : "Not specified",
                    menu.getEveningSnackDetails() != null ? menu.getEveningSnackDetails() : "Not specified"
            );

            // TODO: Integrate with CommsModule (e.g., commsService.sendPushNotificationToAllParents)
            log.info("Dispatched reminder notification content: {}", message);
        } else {
            log.info("Cron execution [18:00]: No menu found for tomorrow ({}). Notification skipped.", tomorrow);
        }
    }
}