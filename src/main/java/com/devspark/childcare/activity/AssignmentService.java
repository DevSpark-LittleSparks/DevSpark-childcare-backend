package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.AssignmentRequestDTO;
import com.devspark.childcare.activity.dto.AssignmentResponseDTO;
import com.devspark.childcare.activity.dto.BatchLogRequestDTO;

import java.util.List;
import java.util.UUID;

public interface AssignmentService {
    AssignmentResponseDTO assignActivityToTeacher(AssignmentRequestDTO request);
    void logProgress(BatchLogRequestDTO request);
    AssignmentResponseDTO publishAssignment(UUID assignmentId);


    AssignmentResponseDTO updateAssignment(UUID assignmentId, AssignmentRequestDTO request);

    List<AssignmentResponseDTO> getTeacherDashboard(UUID teacherId);
    List<AssignmentResponseDTO> getAllAssignments(String date, UUID teacherId);
    void deleteAssignment(UUID assignmentId);
}