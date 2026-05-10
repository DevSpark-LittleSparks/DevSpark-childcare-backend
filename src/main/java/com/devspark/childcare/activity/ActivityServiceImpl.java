package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.ActivityRequestDTO;
import com.devspark.childcare.activity.dto.ActivityResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .map(activity -> new ActivityResponseDTO(
                        activity.getId(),
                        activity.getName(),
                        activity.getCategory(),
                        activity.getDescription(),
                        activity.getMaterialsNeeded()
                )).toList();
    }
}