package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.AssignmentRequestDTO;
import com.devspark.childcare.activity.dto.AssignmentResponseDTO;
import com.devspark.childcare.activity.dto.BatchLogRequestDTO;
import com.devspark.childcare.activity.enums.AssignmentStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final TeacherActivityAssignmentRepository assignmentRepository;
    private final ActivityRepository activityRepository;
    private final ActivityProgressLogRepository progressLogRepository;
    private final EntityManager entityManager;

    private AssignmentResponseDTO mapToDTO(TeacherActivityAssignment assignment) {
        String activityName = "Unknown Activity";
        try {
            activityName = activityRepository.findById(assignment.getActivityId())
                    .map(Activity::getName)
                    .orElse("Unknown Activity");
        } catch (Exception e) {}

        String teacherName = "Assigned Teacher";
        try {
            teacherName = (String) entityManager.createNativeQuery("SELECT full_name FROM teacher WHERE teacher_id = ?")
                    .setParameter(1, assignment.getTeacherId())
                    .getSingleResult();
        } catch (Exception e) {}

        return new AssignmentResponseDTO(
                assignment.getId(),
                assignment.getTeacherId(),
                teacherName,
                assignment.getActivityId(),
                activityName,
                assignment.getAssignedDate(),
                assignment.getStartTime(),
                assignment.getEndTime(),
                assignment.getStatus()
        );
    }

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
        if (currentLoad >= 5) {
            throw new IllegalStateException("Teacher is overloaded. Maximum allowed activities per day is 5");
        }

        TeacherActivityAssignment assignment = TeacherActivityAssignment.builder()
                .teacherId(request.teacherId())
                .activityId(request.activityId())
                .assignedDate(request.assignedDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(AssignmentStatus.DRAFT)
                .currentLoad(currentLoad + 1)
                .deleted(false)
                .build();

        return mapToDTO(assignmentRepository.save(assignment));
    }

    // 👇 අලුතින් එකතු කළා: Update Assignment Logic
    @Override
    @Transactional
    public AssignmentResponseDTO updateAssignment(UUID assignmentId, AssignmentRequestDTO request) {
        if (request.endTime().isBefore(request.startTime()) || request.endTime().equals(request.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        activityRepository.findById(request.activityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        TeacherActivityAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        assignment.setTeacherId(request.teacherId());
        assignment.setActivityId(request.activityId());
        assignment.setAssignedDate(request.assignedDate());
        assignment.setStartTime(request.startTime());
        assignment.setEndTime(request.endTime());

        TeacherActivityAssignment updatedAssignment = assignmentRepository.save(assignment);
        return mapToDTO(updatedAssignment);
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
        return mapToDTO(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getTeacherDashboard(UUID teacherId) {
        return assignmentRepository.findByTeacherIdAndStatusNot(teacherId, AssignmentStatus.DRAFT)
                .stream()
                .map(this::mapToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponseDTO> getAllAssignments(String date, UUID teacherId) {
        List<TeacherActivityAssignment> assignments = assignmentRepository.findAll();

        return assignments.stream()
                .filter(a -> !a.isDeleted())
                .filter(a -> date == null || a.getAssignedDate().toString().equals(date))
                .filter(a -> teacherId == null || a.getTeacherId().equals(teacherId))
                .map(this::mapToDTO).toList();
    }

    @Override
    @Transactional
    public void deleteAssignment(UUID assignmentId) {
        TeacherActivityAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        assignment.setDeleted(true);
        assignmentRepository.save(assignment);
    }
}