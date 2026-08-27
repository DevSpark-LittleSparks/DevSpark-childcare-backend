package com.devspark.childcare.attendance;

import com.devspark.childcare.attendance.dto.BulkAttendanceRequestDTO;
import com.devspark.childcare.attendance.dto.BulkAttendanceResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    List<BulkAttendanceResponseDTO> recordBulkAttendance(BulkAttendanceRequestDTO requestDTO);

    List<BulkAttendanceResponseDTO> getAttendanceByDate(LocalDate date);

    BulkAttendanceResponseDTO quickCheckIn(UUID childId, UUID recordedBy);

    BulkAttendanceResponseDTO quickCheckOut(UUID childId, UUID recordedBy);
}