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

    // 👇 Edit Assignment Endpoint (අලුතින් එකතු කළා)
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AssignmentResponseDTO> updateAssignment(@PathVariable("id") UUID id, @Valid @RequestBody AssignmentRequestDTO request) {
        AssignmentResponseDTO response = assignmentService.updateAssignment(id, request);
        return ApiResponse.success("Assignment updated successfully", response);
    }

    @GetMapping("/teacher/{teacherId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<AssignmentResponseDTO>> getTeacherDashboard(@PathVariable("teacherId") UUID teacherId) {
        return ApiResponse.success("Teacher dashboard data fetched successfully", assignmentService.getTeacherDashboard(teacherId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<AssignmentResponseDTO>> getAssignments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) UUID teacherId) {
        List<AssignmentResponseDTO> assignments = assignmentService.getAllAssignments(date, teacherId);
        return ApiResponse.success("Assignments fetched successfully", assignments);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteAssignment(@PathVariable("id") UUID id) {
        assignmentService.deleteAssignment(id);
        return ApiResponse.success("Assignment deleted successfully", null);
    }
}