package com.devspark.childcare.parent;

import com.devspark.childcare.auth.Parent;
import com.devspark.childcare.auth.ParentRepository;
import com.devspark.childcare.child.Child;
import com.devspark.childcare.child.ChildRepository;
import com.devspark.childcare.parent.dto.AttendanceStatsResponseDto;
import com.devspark.childcare.parent.dto.DailyActivityHoursDto;
import com.devspark.childcare.parent.dto.DailyEngagementDto;
import com.devspark.childcare.parent.dto.ProgressStatsResponseDto;
import com.devspark.childcare.shared.exception.ResourceNotFoundException;
import com.devspark.childcare.shared.exception.UnauthorizedException;
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

@Service
@RequiredArgsConstructor
public class ParentProgressService {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final JdbcTemplate jdbc;

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("MMM d");

    // Confirms the logged-in parent owns this child, returns the child's hex id for raw SQL binding
    private String verifyChildAccess(String email, UUID childId) {
        Parent parent = parentRepository.findByAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Parent profile not found"));

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found"));

        if (!child.getParentId().equals(parent.getParentId())) {
            throw new UnauthorizedException("You do not have access to this child's progress");
        }

        return childId.toString().replace("-", "").toUpperCase();
    }

    public ProgressStatsResponseDto getStats(String email, UUID childId, LocalDate from, LocalDate to) {
        String hex = verifyChildAccess(email, childId);

        int daysPresent = count(
                "SELECT COUNT(*) FROM attendance WHERE HEX(child_id) = ? AND date BETWEEN ? AND ? " +
                "AND status = 'PRESENT' AND deleted = false",
                hex, from, to);

        int activitiesCompleted = count(
                "SELECT COUNT(*) FROM activity_progress_log apl " +
                "JOIN teacher_activity_assignment taa ON HEX(apl.assignment_id) = HEX(taa.assignment_id) AND taa.deleted = false " +
                "WHERE HEX(apl.child_id) = ? AND taa.assigned_date BETWEEN ? AND ? AND apl.deleted = false",
                hex, from, to);

        Map<String, Object> gradeRow = jdbc.queryForMap(
                "SELECT COUNT(*) AS total, " +
                "  COALESCE(SUM(CASE WHEN apl.grading_level = 'LEVEL_1' THEN 1 ELSE 0 END), 0) AS l1_cnt, " +
                "  COALESCE(SUM(CASE WHEN apl.grading_level = 'LEVEL_2' THEN 1 ELSE 0 END), 0) AS l2_cnt, " +
                "  COALESCE(SUM(CASE WHEN apl.grading_level = 'LEVEL_3' THEN 1 ELSE 0 END), 0) AS l3_cnt, " +
                "  COALESCE(SUM(CASE WHEN apl.grading_level = 'LEVEL_4' THEN 1 ELSE 0 END), 0) AS l4_cnt " +
                "FROM activity_progress_log apl " +
                "JOIN teacher_activity_assignment taa ON HEX(apl.assignment_id) = HEX(taa.assignment_id) AND taa.deleted = false " +
                "WHERE HEX(apl.child_id) = ? AND taa.assigned_date BETWEEN ? AND ? AND apl.deleted = false",
                hex, from, to);

        long gradeTotal = ((Number) gradeRow.get("total")).longValue();
        long l1Cnt = ((Number) gradeRow.get("l1_cnt")).longValue();
        long l2Cnt = ((Number) gradeRow.get("l2_cnt")).longValue();
        long l3Cnt = ((Number) gradeRow.get("l3_cnt")).longValue();
        long l4Cnt = ((Number) gradeRow.get("l4_cnt")).longValue();

        int level1Pct = gradeTotal == 0 ? 0 : (int) Math.round(l1Cnt * 100.0 / gradeTotal);
        int level2Pct = gradeTotal == 0 ? 0 : (int) Math.round(l2Cnt * 100.0 / gradeTotal);
        int level3Pct = gradeTotal == 0 ? 0 : (int) Math.round(l3Cnt * 100.0 / gradeTotal);
        int level4Pct = gradeTotal == 0 ? 0 : (int) Math.round(l4Cnt * 100.0 / gradeTotal);

        String avgMood;
        if (gradeTotal == 0) {
            avgMood = null;
        } else {
            double avgGrading = (l1Cnt * 1 + l2Cnt * 2 + l3Cnt * 3 + l4Cnt * 4) / (double) gradeTotal;
            avgMood = Math.round((avgGrading / 4.0) * 100) + "%";
        }

        Map<String, Object> mealRow = jdbc.queryForMap(
                "SELECT COUNT(*) AS total, " +
                "  COALESCE(SUM(CASE WHEN consumption_status = 'FULL_MEAL' THEN 1 ELSE 0 END), 0) AS full_cnt, " +
                "  COALESCE(SUM(CASE WHEN consumption_status = 'PARTIAL'   THEN 1 ELSE 0 END), 0) AS partial_cnt, " +
                "  COALESCE(SUM(CASE WHEN consumption_status = 'ATE_NONE'  THEN 1 ELSE 0 END), 0) AS none_cnt " +
                "FROM meal_consumption_log WHERE HEX(child_id) = ? AND date BETWEEN ? AND ? AND deleted = false",
                hex, from, to);

        long mealTotal = ((Number) mealRow.get("total")).longValue();
        int fullPct    = mealTotal == 0 ? 0 : (int) Math.round(((Number) mealRow.get("full_cnt")).longValue()    * 100.0 / mealTotal);
        int partialPct = mealTotal == 0 ? 0 : (int) Math.round(((Number) mealRow.get("partial_cnt")).longValue() * 100.0 / mealTotal);
        int nonePct    = mealTotal == 0 ? 0 : (int) Math.round(((Number) mealRow.get("none_cnt")).longValue()    * 100.0 / mealTotal);

        String mealsLabel;
        if (mealTotal == 0)     mealsLabel = null;
        else if (fullPct > 75)  mealsLabel = "Full";
        else if (nonePct > 75)  mealsLabel = "None";
        else                    mealsLabel = "Partial";

        return ProgressStatsResponseDto.builder()
                .daysPresent(daysPresent)
                .activitiesCompleted(activitiesCompleted)
                .avgMood(avgMood)
                .level1Percent(level1Pct)
                .level2Percent(level2Pct)
                .level3Percent(level3Pct)
                .level4Percent(level4Pct)
                .mealsProvided(mealsLabel)
                .fullMealPercent(fullPct)
                .partialMealPercent(partialPct)
                .noMealPercent(nonePct)
                .build();
    }

    public List<DailyEngagementDto> getEngagement(String email, UUID childId, LocalDate from, LocalDate to) {
        String hex = verifyChildAccess(email, childId);

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

    public AttendanceStatsResponseDto getAttendance(String email, UUID childId, LocalDate from, LocalDate to) {
        String hex = verifyChildAccess(email, childId);

        int present = count(
                "SELECT COUNT(*) FROM attendance WHERE HEX(child_id) = ? AND date BETWEEN ? AND ? " +
                "AND status = 'PRESENT' AND deleted = false",
                hex, from, to);
        int absent = count(
                "SELECT COUNT(*) FROM attendance WHERE HEX(child_id) = ? AND date BETWEEN ? AND ? " +
                "AND status = 'ABSENT' AND deleted = false",
                hex, from, to);

        int total = present + absent;
        double rate = total == 0 ? 0.0 : (present * 100.0) / total;

        return AttendanceStatsResponseDto.builder()
                .presentDays(present)
                .absentDays(absent)
                .attendanceRate(rate)
                .build();
    }

    private int count(String sql, String hex, LocalDate from, LocalDate to) {
        Integer result = jdbc.queryForObject(sql, Integer.class, hex, from, to);
        return result == null ? 0 : result;
    }
}
