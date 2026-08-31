package com.devspark.childcare.activity;

import com.devspark.childcare.activity.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherActivityAssignmentRepository extends JpaRepository<TeacherActivityAssignment, UUID> {

    @Query("SELECT COUNT(t) > 0 FROM TeacherActivityAssignment t " +
            "WHERE t.teacherId = :teacherId " +
            "AND t.assignedDate = :assignedDate " +
            "AND t.startTime < :endTime " +
            "AND t.endTime > :startTime")
    boolean existsOverlappingAssignment(
            @Param("teacherId") UUID teacherId,
            @Param("assignedDate") LocalDate assignedDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT COUNT(t) FROM TeacherActivityAssignment t " +
            "WHERE t.teacherId = :teacherId " +
            "AND t.assignedDate = :assignedDate " +
            "AND t.status != 'COMPLETED'")
    int countActiveAssignmentsForTeacherOnDate(
            @Param("teacherId") UUID teacherId,
            @Param("assignedDate") LocalDate assignedDate
    );

    // Fetch assignments for teacher dashboard (excluding DRAFTS)
    List<TeacherActivityAssignment> findByTeacherIdAndStatusNot(UUID teacherId, AssignmentStatus status);
}