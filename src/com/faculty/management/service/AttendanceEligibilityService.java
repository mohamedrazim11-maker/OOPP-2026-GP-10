package com.faculty.management.service;

import com.faculty.management.dao.AttendanceDAO;
import com.faculty.management.model.StudentAttendanceSummary;
import com.faculty.management.util.AttendanceCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for computing, filtering, and evaluating student attendance eligibility.
 * Enforces the mandatory 80% threshold across Theory, Practical, and Combined course components.
 *
 * OOP Principles Applied:
 * - Abstraction: High-level eligibility and statistical analysis methods hide SQL querying and raw math.
 * - Encapsulation: Protects statistical aggregation rules and delegating to AttendanceCalculator.
 * - Separation of Concerns: Separates business eligibility rules from session storage (AttendanceService).
 */
public class AttendanceEligibilityService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceEligibilityService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public AttendanceEligibilityService(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO != null ? attendanceDAO : new AttendanceDAO();
    }

    /**
     * Retrieves aggregated attendance summaries for all enrolled students in a course.
     *
     * @param courseCode         Course code (e.g. "ICT2112")
     * @param sessionTypeFilter  "THEORY", "PRACTICAL", or "COMBINED"
     * @return List of StudentAttendanceSummary objects
     */
    public List<StudentAttendanceSummary> getBatchAttendanceSummary(String courseCode, String sessionTypeFilter) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return attendanceDAO.getBatchAttendanceSummary(courseCode.trim().toUpperCase(), sessionTypeFilter);
    }

    /**
     * Filters student summaries by eligibility status ("ALL", "ELIGIBLE", "INELIGIBLE").
     *
     * @param summaries List of summaries
     * @param filter    Eligibility filter choice
     * @return Filtered list of student summaries
     */
    public List<StudentAttendanceSummary> filterSummariesByEligibility(List<StudentAttendanceSummary> summaries, String filter) {
        if (summaries == null) {
            return new ArrayList<>();
        }
        if (filter == null || "ALL".equalsIgnoreCase(filter.trim())) {
            return new ArrayList<>(summaries);
        }

        boolean requireEligible = "ELIGIBLE".equalsIgnoreCase(filter.trim()) || "ELIGIBLE (>= 80%)".equalsIgnoreCase(filter.trim());
        return summaries.stream()
                .filter(s -> requireEligible ? s.isEligible() : !s.isEligible())
                .collect(Collectors.toList());
    }

    /**
     * Computes the batch average attendance percentage across all enrolled students.
     *
     * @param summaries List of student summaries
     * @return Batch average percentage rounded to 1 decimal place
     */
    public double calculateBatchAverage(List<StudentAttendanceSummary> summaries) {
        if (summaries == null || summaries.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (StudentAttendanceSummary s : summaries) {
            sum += s.getPercentage();
        }
        return AttendanceCalculator.roundToOneDecimal(sum / summaries.size());
    }

    /**
     * Counts how many students in the batch satisfy the 80% attendance threshold.
     */
    public int countEligible(List<StudentAttendanceSummary> summaries) {
        if (summaries == null) {
            return 0;
        }
        return (int) summaries.stream().filter(StudentAttendanceSummary::isEligible).count();
    }

    /**
     * Counts how many students in the batch fail to meet the 80% threshold.
     */
    public int countIneligible(List<StudentAttendanceSummary> summaries) {
        if (summaries == null) {
            return 0;
        }
        return (int) summaries.stream().filter(s -> !s.isEligible()).count();
    }
}
