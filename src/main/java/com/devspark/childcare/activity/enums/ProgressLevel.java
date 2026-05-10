package com.devspark.childcare.activity.enums;

/**
 * Represents the 4-color grading system for a child's activity progress.
 */
public enum ProgressLevel {
    EXCELLENT,  // Green
    GOOD,       // Blue
    AVERAGE,    // Yellow
    NEEDS_HELP, // Red
    ABSENT,     // Auto-assigned if child is absent
    PENDING     // Default status before teacher grades
}