package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.ActivityRequestDTO;
import com.devspark.childcare.activity.dto.ActivityResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devspark.childcare.activity.enums.ActivityCategory;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Override
    @Transactional
    public ActivityResponseDTO createActivity(ActivityRequestDTO request) {
        Activity activity = Activity.builder()
                .name(request.name())
                .category(request.category())
                .description(request.description())
                .materialsNeeded(request.materialsNeeded())
                .deleted(false)
                .build();

        Activity savedActivity = activityRepository.save(activity);

        return new ActivityResponseDTO(
                savedActivity.getId(),
                savedActivity.getName(),
                savedActivity.getCategory(),
                savedActivity.getDescription(),
                savedActivity.getMaterialsNeeded()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getAllActivities() {
        return activityRepository.findAll().stream()
                .filter(activity -> !activity.isDeleted()) // 💡 Delete කරපු ඒවා පෙන්නන්නේ නැති වෙන්න හැදුවා
                .map(activity -> new ActivityResponseDTO(
                        activity.getId(),
                        activity.getName(),
                        activity.getCategory(),
                        activity.getDescription(),
                        activity.getMaterialsNeeded()
                )).toList();
    }

    // 👇 Edit (Update) Logic එක
    @Override
    @Transactional
    public ActivityResponseDTO updateActivity(UUID id, ActivityRequestDTO request) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        activity.setName(request.name());
        activity.setCategory(request.category());
        activity.setDescription(request.description());
        activity.setMaterialsNeeded(request.materialsNeeded());

        Activity updatedActivity = activityRepository.save(activity);

        return new ActivityResponseDTO(
                updatedActivity.getId(),
                updatedActivity.getName(),
                updatedActivity.getCategory(),
                updatedActivity.getDescription(),
                updatedActivity.getMaterialsNeeded()
        );
    }

    // 👇 Soft Delete Logic එක
    @Override
    @Transactional
    public void deleteActivity(UUID id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        activity.setDeleted(true);
        activityRepository.save(activity);
    }
}