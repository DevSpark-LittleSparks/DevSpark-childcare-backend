package com.devspark.childcare.activity;

import com.devspark.childcare.activity.dto.AssignmentRequestDTO;
import com.devspark.childcare.activity.dto.AssignmentResponseDTO;
import com.devspark.childcare.activity.dto.BatchLogRequestDTO;
import com.devspark.childcare.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AssignmentResponseDTO> assignActivity(@Valid @RequestBody AssignmentRequestDTO request) {
        AssignmentResponseDTO response = assignmentService.assignActivityToTeacher(request);
        return ApiResponse.success("Activity successfully assigned as DRAFT", response);
    }

    @PostMapping("/log-progress")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> logProgress(@Valid @RequestBody BatchLogRequestDTO request) {
        assignmentService.logProgress(request);
        return ApiResponse.success("Progress logs saved and assignment marked as COMPLETED", null);
    }

    @PutMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AssignmentResponseDTO> publishAssignment(@PathVariable("id") UUID id) {
        AssignmentResponseDTO response = assignmentService.publishAssignment(id);
        return ApiResponse.success("Assignment published successfully", response);
    }

    @GetMapping("/teacher/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<AssignmentResponseDTO>> getTeacherDashboard(@PathVariable("teacherId") UUID teacherId) {
        return ApiResponse.success("Teacher dashboard data fetched successfully", assignmentService.getTeacherDashboard(teacherId));
    }
}