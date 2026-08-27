package com.devspark.childcare.attendance;

import com.devspark.childcare.attendance.dto.BulkAttendanceResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private UUID childId;
    private UUID teacherId;

    @BeforeEach
    void setUp() {
        childId = UUID.randomUUID();
        teacherId = UUID.randomUUID();
    }

    @Test
    void shouldSuccessfullyQuickCheckIn() {
        // 1. Arrange: Prepare mock data
        LocalDate today = LocalDate.now();
        Attendance mockAttendance = Attendance.builder()
                .childId(childId)
                .date(today)
                .status(AttendanceStatus.PRESENT)
                .build();

        // Simulate database behavior: if not found, return empty; then return saved record
        when(attendanceRepository.findByChildIdAndDate(childId, today)).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(mockAttendance);

        // 2. Act: Call the service method
        BulkAttendanceResponseDTO response = attendanceService.quickCheckIn(childId, teacherId);

        // 3. Assert: Check if the results are what we expect
        assertNotNull(response);
        assertEquals(AttendanceStatus.PRESENT, response.getStatus());

        // Verify that the repository's save method was actually called once
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void shouldThrowExceptionWhenCheckOutWithoutCheckIn() {
        // 1. Arrange: No record exists for today
        when(attendanceRepository.findByChildIdAndDate(any(), any())).thenReturn(Optional.empty());

        // 2. Act & Assert: It should throw an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            attendanceService.quickCheckOut(childId, teacherId);
        });
    }
}