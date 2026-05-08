package com.devspark.childcare.attendance;

import com.devspark.childcare.attendance.dto.BulkAttendanceRequestDTO;
import com.devspark.childcare.attendance.dto.BulkAttendanceResponseDTO;
import com.devspark.childcare.attendance.dto.ChildAttendanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Override
    @Transactional
    public List<BulkAttendanceResponseDTO> recordBulkAttendance(BulkAttendanceRequestDTO requestDTO) {
        List<Attendance> attendancesToSave = new ArrayList<>();

        for (ChildAttendanceDTO record : requestDTO.getAttendances()) {

            // RED ALERT VALIDATION: Ensure every record has a status
            if (record.getStatus() == null) {
                throw new IllegalArgumentException("Validation Error: Missing attendance status for child ID " + record.getChildId());
            }

            Optional<Attendance> existing = attendanceRepository.findByChildIdAndDate(
                    record.getChildId(), requestDTO.getDate()
            );

            if (existing.isPresent()) {
                Attendance att = existing.get();
                att.setStatus(record.getStatus());
                att.setNotes(record.getNotes());
                att.setRecordedBy(requestDTO.getRecordedBy());
                // Note: We deliberately DO NOT overwrite checkIn and checkOut times here.
                attendancesToSave.add(att);
            } else {
                Attendance newAtt = Attendance.builder()
                        .childId(record.getChildId())
                        .recordedBy(requestDTO.getRecordedBy())
                        .date(requestDTO.getDate())
                        .status(record.getStatus())
                        .notes(record.getNotes())
                        .build();
                attendancesToSave.add(newAtt);
            }
        }

        List<Attendance> savedList = attendanceRepository.saveAll(attendancesToSave);
        return savedList.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<BulkAttendanceResponseDTO> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BulkAttendanceResponseDTO quickCheckIn(UUID childId, UUID recordedBy) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Optional<Attendance> existingRecord = attendanceRepository.findByChildIdAndDate(childId, today);

        Attendance attendance;
        if (existingRecord.isPresent()) {
            attendance = existingRecord.get();
            // Prevent overwriting original check-in time if clicked multiple times
            if (attendance.getCheckIn() == null) {
                attendance.setCheckIn(now);
            }
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setRecordedBy(recordedBy);
        } else {
            attendance = Attendance.builder()
                    .childId(childId)
                    .recordedBy(recordedBy)
                    .date(today)
                    .checkIn(now)
                    .status(AttendanceStatus.PRESENT)
                    .build();
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToResponseDTO(savedAttendance);
    }

    @Override
    @Transactional
    public BulkAttendanceResponseDTO quickCheckOut(UUID childId, UUID recordedBy) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance attendance = attendanceRepository.findByChildIdAndDate(childId, today)
                .orElseThrow(() -> new IllegalArgumentException("Cannot check-out: No check-in record found for child today."));

        attendance.setCheckOut(now);
        attendance.setRecordedBy(recordedBy);

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToResponseDTO(savedAttendance);
    }

    // Helper method to map Entity to DTO (Now includes checkIn and checkOut)
    private BulkAttendanceResponseDTO mapToResponseDTO(Attendance attendance) {
        return BulkAttendanceResponseDTO.builder()
                .id(attendance.getId())
                .childId(attendance.getChildId())
                .status(attendance.getStatus())
                .date(attendance.getDate())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .notes(attendance.getNotes())
                .build();
    }
}