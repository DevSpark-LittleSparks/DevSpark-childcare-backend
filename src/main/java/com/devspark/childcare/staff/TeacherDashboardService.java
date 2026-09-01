package com.devspark.childcare.staff;

import com.devspark.childcare.shared.exception.ResourceNotFoundException;
import com.devspark.childcare.staff.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private final TeacherRepository teacherRepo;
    private final JdbcTemplate      jdbc;

    private Teacher getTeacher(String email) {
        return teacherRepo.findByAccountEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + email));
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    public ClassStatusDto getClassStatus(String email) {
        Teacher teacher = getTeacher(email);
        int expected = jdbc.queryForObject(
            "SELECT COUNT(*) FROM child WHERE deleted = false", Integer.class);
        int checkedIn = jdbc.queryForObject(
            "SELECT COUNT(*) FROM attendance WHERE recorded_by = ? AND date = ? AND status = 'PRESENT' AND deleted = false",
            Integer.class, teacher.getTeacherId(), LocalDate.now());
        int pct = expected > 0 ? (checkedIn * 100 / expected) : 0;
        return ClassStatusDto.builder()
                .checkedIn(checkedIn).expected(expected)
                .checkedInPercent(pct).expectedPercent(expected > 0 ? 100 : 0)
                .build();
    }

    public List<SafetyAlertDto> getSafetyAlerts(String email) {
        return jdbc.query(
            "SELECT first_name, last_name, special_note FROM child " +
            "WHERE special_note IS NOT NULL AND special_note != '' AND deleted = false ORDER BY first_name",
            (rs, i) -> SafetyAlertDto.builder()
                    .childName(rs.getString("first_name") + " " + rs.getString("last_name"))
                    .condition(extractCondition(rs.getString("special_note")))
                    .build());
    }

    private String extractCondition(String note) {
        if (note == null) return "Medical Note";
        String[] parts = note.split("[-,]", 2);
        String label = parts[0].trim();
        return label.length() > 50 ? label.substring(0, 50) + "…" : label;
    }

    public List<ParentMessageDto> getParentMessages(String email) {
        Teacher teacher = getTeacher(email);
        String hex = teacher.getAccount().getAccountId().toString().replace("-", "").toUpperCase();

        return jdbc.query(
            "SELECT p.full_name AS parent_name, cm.content AS latest_content, " +
            "  DATE_FORMAT(cm.sent_at, '%h:%i %p') AS msg_time, " +
            "  (HEX(cm.sender_id) != ?) AS is_unread " +
            "FROM chat_thread ct " +
            "JOIN chat_message cm ON cm.message_id = (" +
            "  SELECT m2.message_id FROM chat_message m2 " +
            "  WHERE m2.thread_id = ct.thread_id AND m2.deleted = false " +
            "  ORDER BY m2.sent_at DESC LIMIT 1) " +
            "JOIN account pa ON (HEX(pa.account_id) = HEX(ct.participant_one) OR HEX(pa.account_id) = HEX(ct.participant_two)) " +
            "  AND HEX(pa.account_id) != ? AND pa.role = 'PARENT' AND pa.deleted = false " +
            "JOIN parent p ON p.account_id = pa.account_id AND p.deleted = false " +
            "WHERE (HEX(ct.participant_one) = ? OR HEX(ct.participant_two) = ?) AND ct.deleted = false " +
            "ORDER BY cm.sent_at DESC LIMIT 3",
            (rs, i) -> ParentMessageDto.builder()
                    .parentName(rs.getString("parent_name"))
                    .preview(truncate(rs.getString("latest_content"), 60))
                    .time(rs.getString("msg_time"))
                    .unread(rs.getBoolean("is_unread"))
                    .build(),
            hex, hex, hex, hex);
    }

    public List<UpcomingActivityDto> getUpcomingActivities(String email) {
        Teacher teacher = getTeacher(email);
        String hex = teacher.getTeacherId().toString().replace("-", "").toUpperCase();

        return jdbc.query(
            "SELECT TIME_FORMAT(taa.start_time,'%h:%i %p') AS start_fmt, " +
            "  TIME_FORMAT(taa.end_time,'%h:%i %p') AS end_fmt, " +
            "  a.activity_name, a.description, taa.status " +
            "FROM teacher_activity_assignment taa " +
            "JOIN activity a ON HEX(taa.activity_id) = HEX(a.activity_id) AND a.deleted = false " +
            "WHERE HEX(taa.teacher_id) = ? AND taa.assigned_date = ? AND taa.deleted = false " +
            "ORDER BY taa.start_time ASC",
            (rs, i) -> UpcomingActivityDto.builder()
                    .startTime(rs.getString("start_fmt")).endTime(rs.getString("end_fmt"))
                    .name(rs.getString("activity_name")).description(rs.getString("description"))
                    .status(rs.getString("status")).build(),
            hex, LocalDate.now());
    }

    public List<ActivityLogDto> getActivityLogs(String email, String sortBy) {
        Teacher teacher = getTeacher(email);
        String hex = teacher.getTeacherId().toString().replace("-", "").toUpperCase();

        List<ActivityLogDto> meals = jdbc.query(
            "SELECT mcl.consumption_id, CONCAT(c.first_name,' ',c.last_name) AS child_name, " +
            "  c.profile_pic, CONCAT(mcl.meal_type,' - ',mcl.consumption_status) AS detail, " +
            "  DATE_FORMAT(mcl.created_at,'%h:%i %p') AS log_time " +
            "FROM meal_consumption_log mcl " +
            "JOIN child c ON HEX(mcl.child_id) = HEX(c.child_id) AND c.deleted = false " +
            "WHERE mcl.date = ? AND mcl.deleted = false ORDER BY mcl.created_at DESC",
            (rs, i) -> ActivityLogDto.builder()
                    .logId(rs.getString("consumption_id"))
                    .childName(rs.getString("child_name")).childImage(rs.getString("profile_pic"))
                    .logType("MEAL").detail(rs.getString("detail").replace("_", " "))
                    .time(rs.getString("log_time")).build(),
            LocalDate.now());

        List<ActivityLogDto> acts = jdbc.query(
            "SELECT apl.log_id, CONCAT(c.first_name,' ',c.last_name) AS child_name, " +
            "  c.profile_pic, CONCAT(a.activity_name,' - ',apl.grading_level) AS detail, " +
            "  DATE_FORMAT(apl.created_at,'%h:%i %p') AS log_time " +
            "FROM activity_progress_log apl " +
            "JOIN teacher_activity_assignment taa ON HEX(apl.assignment_id) = HEX(taa.assignment_id) " +
            "  AND HEX(taa.teacher_id) = ? AND taa.assigned_date = ? AND taa.deleted = false " +
            "JOIN activity a ON HEX(taa.activity_id) = HEX(a.activity_id) AND a.deleted = false " +
            "JOIN child c ON HEX(apl.child_id) = HEX(c.child_id) AND c.deleted = false " +
            "WHERE apl.deleted = false ORDER BY apl.created_at DESC",
            (rs, i) -> ActivityLogDto.builder()
                    .logId(rs.getString("log_id"))
                    .childName(rs.getString("child_name")).childImage(rs.getString("profile_pic"))
                    .logType("ACTIVITY").detail(formatGrading(rs.getString("detail")))
                    .time(rs.getString("log_time")).build(),
            hex, LocalDate.now());

        List<ActivityLogDto> all = new ArrayList<>();
        all.addAll(meals); all.addAll(acts);

        if ("name_asc".equals(sortBy))      all.sort(Comparator.comparing(ActivityLogDto::getChildName));
        else if ("name_desc".equals(sortBy)) all.sort(Comparator.comparing(ActivityLogDto::getChildName).reversed());
        else if ("type_meal".equals(sortBy)) all.sort(Comparator.comparing(d -> "MEAL".equals(d.getLogType()) ? 0 : 1));
        else if ("type_activity".equals(sortBy)) all.sort(Comparator.comparing(d -> "ACTIVITY".equals(d.getLogType()) ? 0 : 1));
        else all.sort(Comparator.comparing(ActivityLogDto::getTime).reversed());

        return all;
    }

    private String formatGrading(String raw) {
        if (raw == null) return "";
        return raw.replace("LEVEL_1","Weak").replace("LEVEL_2","Good")
                  .replace("LEVEL_3","Very Good").replace("LEVEL_4","Excellent");
    }
}