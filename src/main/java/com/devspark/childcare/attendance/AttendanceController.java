package com.devspark.childcare.attendance;

import com.devspark.childcare.attendance.dto.BulkAttendanceRequestDTO;
import com.devspark.childcare.attendance.dto.BulkAttendanceResponseDTO;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<BulkAttendanceResponseDTO>>> recordBulkAttendance(
            @RequestBody BulkAttendanceRequestDTO requestDTO) {

        List<BulkAttendanceResponseDTO> response = attendanceService.recordBulkAttendance(requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Bulk attendance recorded successfully", response));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<BulkAttendanceResponseDTO>>> getAttendanceByDate(
            @PathVariable LocalDate date) {

        List<BulkAttendanceResponseDTO> response = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Daily attendance fetched successfully", response));
    }

    @PatchMapping("/{childId}/check-in")
    public ResponseEntity<ApiResponse<BulkAttendanceResponseDTO>> quickCheckIn(
            @PathVariable UUID childId,
            @RequestParam UUID recordedBy) {

        BulkAttendanceResponseDTO response = attendanceService.quickCheckIn(childId, recordedBy);
        return ResponseEntity.ok(ApiResponse.success("Child checked in successfully", response));
    }

    @PatchMapping("/{childId}/check-out")
    public ResponseEntity<ApiResponse<BulkAttendanceResponseDTO>> quickCheckOut(
            @PathVariable UUID childId,
            @RequestParam UUID recordedBy) {

        BulkAttendanceResponseDTO response = attendanceService.quickCheckOut(childId, recordedBy);
        return ResponseEntity.ok(ApiResponse.success("Child checked out successfully", response));
    }
}