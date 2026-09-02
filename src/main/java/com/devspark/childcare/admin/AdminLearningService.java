package com.devspark.childcare.admin;

import com.devspark.childcare.admin.dto.DailyActivityDto;
import com.devspark.childcare.admin.dto.DailyProgressEntryDto;
import com.devspark.childcare.child.Child;
import com.devspark.childcare.child.ChildRepository;
import com.devspark.childcare.child.dto.ChildSummaryDto;
import com.devspark.childcare.parent.dto.AttendanceStatsResponseDto;
import com.devspark.childcare.parent.dto.DailyActivityHoursDto;
import com.devspark.childcare.parent.dto.DailyEngagementDto;
import com.devspark.childcare.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminLearningService {

    private final ChildRepository childRepository;
    private final JdbcTemplate jdbc;

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("MMM d");

    public List<DailyProgressEntryDto> getDailyProgress(LocalDate date) {
        // Teachers grade using the EXCELLENT/GOOD/AVERAGE/NEEDS_HELP scale today;
        // LEVEL_1-4 is the older scale kept only for historical rows (see
        // V35__Update_progress_level_enum.sql). Both scales share the same
        // 1 (weakest) - 4 (best) meaning, so each new label is folded into the
        // matching legacy tier rather than being counted separately.
        Map<String, Object> row = jdbc.queryForMap(
                "SELECT " +
                "  COALESCE(SUM(CASE WHEN apl.grading_level IN ('LEVEL_4', 'EXCELLENT') THEN 1 ELSE 0 END), 0) AS excellent, " +
                "  COALESCE(SUM(CASE WHEN apl.grading_level IN ('LEVEL_3', 'GOOD')      THEN 1 ELSE 0 END), 0) AS very_good, " +
                "  COALESCE(SUM(CASE WHEN apl.grading_level IN ('LEVEL_2', 'AVERAGE')   THEN 1 ELSE 0 END), 0) AS good, " +
                "  COALESCE(SUM(CASE WHEN apl.grading_level IN ('LEVEL_1', 'NEEDS_HELP') THEN 1 ELSE 0 END), 0) AS weak " +
                "FROM activity_progress_log apl " +
                "JOIN teacher_activity_assignment taa ON HEX(apl.assignment_id) = HEX(taa.assignment_id) AND taa.deleted = false " +
                "WHERE taa.assigned_date = ? AND apl.deleted = false " +
                "AND apl.grading_level NOT IN ('PENDING', 'ABSENT')",
                date);

        int excellent = ((Number) row.get("excellent")).intValue();
        int good      = ((Number) row.get("good")).intValue();
        int veryGood  = ((Number) row.get("very_good")).intValue();
        int weak      = ((Number) row.get("weak")).intValue();

        if (excellent == 0 && good == 0 && veryGood == 0 && weak == 0) {
            return List.of();
        }

        DailyProgressEntryDto entry = DailyProgressEntryDto.builder()
                .date(date.toString())
                .excellent(excellent)
                .good(good)
                .veryGood(veryGood)
                .weak(weak)
                .build();

        return List.of(entry);
    }

    public List<DailyActivityDto> getDailyActivities(LocalDate date) {
        return jdbc.query(
                "SELECT a.activity_name AS activity_name, " +
                "  TIME_FORMAT(taa.start_time, '%h:%i %p') AS time_fmt, " +
                "  t.full_name AS teacher_name, t.designation AS designation, " +
                "  COUNT(apl.log_id) AS student_count " +
                "FROM teacher_activity_assignment taa " +
                "JOIN activity a ON HEX(taa.activity_id) = HEX(a.activity_id) AND a.deleted = false " +
                "JOIN teacher t ON HEX(taa.teacher_id) = HEX(t.teacher_id) AND t.deleted = false " +
                "LEFT JOIN activity_progress_log apl ON HEX(apl.assignment_id) = HEX(taa.assignment_id) AND apl.deleted = false " +
                "WHERE taa.assigned_date = ? AND taa.deleted = false " +
                "GROUP BY taa.assignment_id, a.activity_name, taa.start_time, t.full_name, t.designation " +
                "ORDER BY taa.start_time ASC",
                (rs, i) -> {
                    int studentCount = rs.getInt("student_count");
                    String designation = rs.getString("designation");
                    return DailyActivityDto.builder()
                            .activityName(rs.getString("activity_name"))
                            .time(rs.getString("time_fmt"))
                            .teacherName(rs.getString("teacher_name"))
                            .teacherRole("SENIOR".equals(designation) ? "Senior Teacher" : "Junior Teacher")
                            .studentCount(studentCount)
                            .participationStatus(studentCount > 0 ? "Participated" : "Not Participated")
                            .build();
                },
                date);
    }

    public List<ChildSummaryDto> getAllChildren() {
        return childRepository.findAll().stream()
                .map(c -> ChildSummaryDto.builder()
                        .childId(c.getChildId())
                        .name(c.getFirstName() + " " + c.getLastName())
                        .profilePic(c.getProfilePic())
                        .status(c.getStatus().name())
                        .dob(c.getDob() != null ? c.getDob().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    public List<DailyEngagementDto> getChildEngagement(UUID childId, LocalDate from, LocalDate to) {
        String hex = verifyChildExists(childId);

        Map<LocalDate, List<DailyActivityHoursDto>> activitiesByDate = new LinkedHashMap<>();
        jdbc.query(
                "SELECT taa.assigned_date AS d, a.activity_name AS activity_name, " +
                "  SUM(TIME_TO_SEC(TIMEDIFF(taa.end_time, taa.start_time))) / 3600.0 AS hrs " +
                "FROM activity_progress_log apl " +
                "JOIN teacher_activity_assignment taa ON HEX(apl.assignment_id) = HEX(taa.assignment_id) AND taa.deleted = false " +
                "JOIN activity a ON HEX(taa.activity_id) = HEX(a.activity_id) AND a.deleted = false " +
                "WHERE HEX(apl.child_id) = ? AND taa.assigned_date BETWEEN ? AND ? AND apl.deleted = false " +
                "GROUP BY taa.assigned_date, a.activity_name",
                (RowCallbackHandler) rs -> {
                    LocalDate d = rs.getDate("d").toLocalDate();
                    DailyActivityHoursDto entry = DailyActivityHoursDto.builder()
                            .activityName(rs.getString("activity_name"))
                            .hours(Math.round(rs.getDouble("hrs") * 10) / 10.0)
                            .build();
                    activitiesByDate.computeIfAbsent(d, k -> new ArrayList<>()).add(entry);
                },
                hex, from, to);

        List<DailyEngagementDto> result = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            result.add(DailyEngagementDto.builder()
                    .day(d.format(DAY_LABEL))
                    .date(d.toString())
                    .activities(activitiesByDate.getOrDefault(d, List.of()))
                    .build());
        }
        return result;
    }

    public AttendanceStatsResponseDto getChildAttendance(UUID childId, LocalDate from, LocalDate to) {
        String hex = verifyChildExists(childId);

        int present = count(
                "SELECT COUNT(*) FROM attendance WHERE HEX(child_id) = ? AND date BETWEEN ? AND ? " +
                "AND status = 'PRESENT' AND deleted = false",
                hex, from, to);
        int absent = count(
                "SELECT COUNT(*) FROM attendance WHERE HEX(child_id) = ? AND date BETWEEN ? AND ? " +
                "AND status = 'ABSENT' AND deleted = false",
                hex, from, to);
        int halfDay = count(
                "SELECT COUNT(*) FROM attendance WHERE HEX(child_id) = ? AND date BETWEEN ? AND ? " +
                "AND status = 'HALF_DAY' AND deleted = false",
                hex, from, to);

        // See ParentProgressService.getAttendance for why HALF_DAY needs its own
        // bucket: left out entirely, it silently vanished from both the count
        // and the rate, so a child with only half-days and no ABSENT records
        // showed as 100% present.
        int total = present + absent + halfDay;
        double rate = total == 0 ? 0.0 : ((present + halfDay * 0.5) * 100.0) / total;

        return AttendanceStatsResponseDto.builder()
                .presentDays(present)
                .absentDays(absent)
                .halfDays(halfDay)
                .attendanceRate(rate)
                .build();
    }

    private String verifyChildExists(UUID childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found"));
        return child.getChildId().toString().replace("-", "").toUpperCase();
    }

    private int count(String sql, String hex, LocalDate from, LocalDate to) {
        Integer result = jdbc.queryForObject(sql, Integer.class, hex, from, to);
        return result == null ? 0 : result;
    }
}
