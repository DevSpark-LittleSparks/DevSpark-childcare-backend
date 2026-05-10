package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.AssignmentRequestDTO;
import com.devspark.childcare.activity.dto.AssignmentResponseDTO;
import com.devspark.childcare.activity.dto.BatchLogRequestDTO;
import com.devspark.childcare.activity.enums.AssignmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final TeacherActivityAssignmentRepository assignmentRepository;
    private final ActivityRepository activityRepository;
    private final ActivityProgressLogRepository progressLogRepository;

    @Override
    @Transactional
    public AssignmentResponseDTO assignActivityToTeacher(AssignmentRequestDTO request) {

        if (request.endTime().isBefore(request.startTime()) || request.endTime().equals(request.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        activityRepository.findById(request.activityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        boolean hasOverlap = assignmentRepository.existsOverlappingAssignment(
                request.teacherId(), request.assignedDate(), request.startTime(), request.endTime()
        );
        if (hasOverlap) {
            throw new IllegalStateException("Teacher already has another activity scheduled during this time.");
        }

        int currentLoad = assignmentRepository.countActiveAssignmentsForTeacherOnDate(
                request.teacherId(), request.assignedDate()
        );

        int MAX_DAILY_ACTIVITIES = 5;
        if (currentLoad >= MAX_DAILY_ACTIVITIES) {
            throw new IllegalStateException("Teacher is overloaded. Maximum allowed activities per day is " + MAX_DAILY_ACTIVITIES);
        }

        TeacherActivityAssignment assignment = TeacherActivityAssignment.builder()
                .teacherId(request.teacherId())
                .activityId(request.activityId())
                .assignedDate(request.assignedDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(AssignmentStatus.DRAFT) // Saves as DRAFT initially
                .currentLoad(currentLoad + 1)
                .deleted(false)
                .build();

        TeacherActivityAssignment savedAssignment = assignmentRepository.save(assignment);

        return new AssignmentResponseDTO(
                savedAssignment.getId(),
                savedAssignment.getTeacherId(),
                savedAssignment.getActivityId(),
                savedAssignment.getAssignedDate(),
                savedAssignment.getStartTime(),
                savedAssignment.getEndTime(),
                savedAssignment.getStatus()
        );
    }

    @Override
    @Transactional
    public void logProgress(BatchLogRequestDTO request) {
        TeacherActivityAssignment assignment = assignmentRepository.findById(request.assignmentId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        request.logs().forEach(logRequest -> {
            ActivityProgressLog progressLog = ActivityProgressLog.builder()
                    .assignmentId(assignment.getId())
                    .childId(logRequest.childId())
                    .gradingLevel(logRequest.progressLevel())
                    .note(logRequest.note())
                    .deleted(false)
                    .build();
            progressLogRepository.save(progressLog);
        });

        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public AssignmentResponseDTO publishAssignment(UUID assignmentId) {
        TeacherActivityAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (assignment.getStatus() != AssignmentStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT assignments can be published");
        }

        assignment.setStatus(AssignmentStatus.ASSIGNED);
        TeacherActivityAssignment updatedAssignment = assignmentRepository.save(assignment);

        return new AssignmentResponseDTO(
                updatedAssignment.getId(),
                updatedAssignment.getTeacherId(),
                updatedAssignment.getActivityId(),
                updatedAssignment.getAssignedDate(),
                updatedAssignment.getStartTime(),
                updatedAssignment.getEndTime(),
                updatedAssignment.getStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getTeacherDashboard(UUID teacherId) {
        return assignmentRepository.findByTeacherIdAndStatusNot(teacherId, AssignmentStatus.DRAFT)
                .stream()
                .map(assignment -> new AssignmentResponseDTO(
                        assignment.getId(),
                        assignment.getTeacherId(),
                        assignment.getActivityId(),
                        assignment.getAssignedDate(),
                        assignment.getStartTime(),
                        assignment.getEndTime(),
                        assignment.getStatus()
                )).toList();
    }
}