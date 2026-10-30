package com.faculty.management.util;

/**
 * Utility class for attendance evaluations, percentage determination,
 * effective attendance calculations with approved medical submissions,
 * and 80% examination eligibility checks.
 *
 * Requirements & Academic Rules (Faculty of Technology):
 * - Theory Sessions: 15 sessions per semester
 * - Practical Sessions: 15 sessions per semester
 * - Minimum Attendance Threshold: 80.0% required to sit final course examinations
 * - Effective Attendance: Physically attended sessions + Approved medical submissions
 *   (effective count is capped at total sessions held)
 *
 * OOP Principles Applied:
 * - Abstraction: Encapsulates academic regulations and formulas behind simple static methods.
 * - Encapsulation: Private constructor prevents instantiation; constant thresholds protect rules.
 */
public class AttendanceCalculator {

    /**
     * Minimum attendance percentage required for examination eligibility (80.0%).
     */
    public static final double ELIGIBILITY_THRESHOLD = 80.0;

    /**
     * Standard total sessions per course component (Theory / Practical) in a semester.
     */
    public static final int STANDARD_COMPONENT_SESSIONS = 15;

    /**
     * Standard total combined sessions (Theory + Practical) in a semester.
     */
    public static final int STANDARD_COMBINED_SESSIONS = 30;

    private AttendanceCalculator() {
        // Prevent instantiation
    }

    /**
     * Calculates the raw attendance percentage based on attended and total sessions held.
     *
     * @param attended      Number of sessions physically attended
     * @param totalSessions Total number of sessions held
     * @return Attendance percentage rounded to 1 decimal place (0.0 - 100.0)
     */
    public static double calculatePercentage(int attended, int totalSessions) {
        if (totalSessions <= 0) {
            return 0.0;
        }
        int validAttended = Math.max(0, Math.min(attended, totalSessions));
        double raw = ((double) validAttended / totalSessions) * 100.0;
        return roundToOneDecimal(raw);
    }

    /**
     * Calculates effective attended sessions, which adds approved medical absences
     * to physical attendance, capped at the total number of sessions.
     *
     * @param attended         Physically attended session count
     * @param approvedMedicals Approved medical certificate count
     * @param totalSessions    Total sessions held
     * @return Effective attended session count (never exceeds totalSessions)
     */
    public static int calculateEffectiveAttended(int attended, int approvedMedicals, int totalSessions) {
        if (totalSessions <= 0) {
            return 0;
        }
        int safeAttended = Math.max(0, attended);
        int safeMedicals = Math.max(0, approvedMedicals);
        return Math.min(totalSessions, safeAttended + safeMedicals);
    }

    /**
     * Calculates the effective attendance percentage, factoring in approved medical certificates.
     *
     * @param attended         Physically attended session count
     * @param approvedMedicals Approved medical certificate count
     * @param totalSessions    Total sessions held
     * @return Effective attendance percentage rounded to 1 decimal place
     */
    public static double calculateEffectivePercentage(int attended, int approvedMedicals, int totalSessions) {
        if (totalSessions <= 0) {
            return 0.0;
        }
        int effective = calculateEffectiveAttended(attended, approvedMedicals, totalSessions);
        double raw = ((double) effective / totalSessions) * 100.0;
        return roundToOneDecimal(raw);
    }

    /**
     * Checks whether the attendance percentage satisfies the mandatory 80% threshold.
     *
     * @param percentage Attendance percentage
     * @return true if percentage >= 80.0%, false otherwise
     */
    public static boolean isEligible(double percentage) {
        return percentage >= ELIGIBILITY_THRESHOLD;
    }

    /**
     * Evaluates eligibility directly from session counts including medicals.
     *
     * @param attended         Physically attended session count
     * @param approvedMedicals Approved medical certificate count
     * @param totalSessions    Total sessions held
     * @return true if effective percentage >= 80.0%
     */
    public static boolean isEligible(int attended, int approvedMedicals, int totalSessions) {
        double percentage = calculateEffectivePercentage(attended, approvedMedicals, totalSessions);
        return isEligible(percentage);
    }

    /**
     * Returns a user-friendly eligibility status string.
     *
     * @param percentage Attendance percentage
     * @return "ELIGIBLE (>= 80%)" or "NOT ELIGIBLE (< 80%)"
     */
    public static String getEligibilityStatus(double percentage) {
        return isEligible(percentage) ? "ELIGIBLE (>= 80%)" : "NOT ELIGIBLE (< 80%)";
    }

    /**
     * Rounds a double value to one decimal place (e.g. 84.56 -> 84.6).
     */
    public static double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
