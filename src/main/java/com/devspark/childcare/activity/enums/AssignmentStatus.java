package com.devspark.childcare.activity.enums;

/**
 * Represents the current status of an activity assigned to a teacher.
 * Follows the Master Plan strict states.
 */
public enum AssignmentStatus {
    DRAFT,      // Saved by Admin, but not visible to teacher yet
    ASSIGNED,   // Published by Admin, visible to teacher on their dashboard
    COMPLETED,   // Teacher has logged progress for all students
    PENDING
}