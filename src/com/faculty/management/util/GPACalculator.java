package com.faculty.management.util;

import com.faculty.management.model.Grade;

import java.util.List;

/**
 * Computes SGPA and CGPA using the standard credit-weighted formula:
 *
 *     SGPA / CGPA = Σ(Credit × Grade Point) / Σ Credit
 *
 * Courses where the student was not CA-eligible (Grade.countsTowardGpa()
 * == false) are excluded from both the numerator and the denominator,
 * consistent with a "Not Eligible" course not having been completed.
 *
 * Demonstrates Abstraction: SGPA and CGPA are the exact same weighted-average
 * calculation — the only difference is which list of Grades is passed in
 * (one semester's grades for SGPA, every semester's grades for CGPA).
 */
public class GPACalculator {

    private GPACalculator() {
        // Prevent instantiation
    }

    /** SGPA for a single semester, from that semester's list of course Grades. */
    public static double computeSgpa(List<Grade> semesterGrades) {
        return computeWeightedGpa(semesterGrades);
    }

    /** CGPA across every course Grade a student has ever received, regardless of semester. */
    public static double computeCgpa(List<Grade> allGrades) {
        return computeWeightedGpa(allGrades);
    }

    private static double computeWeightedGpa(List<Grade> grades) {
        double weightedSum = 0;
        int totalCredits = 0;
        for (Grade grade : grades) {
            if (grade.countsTowardGpa() && grade.getCourse() != null) {
                int credits = grade.getCourse().getCredits();
                weightedSum += credits * grade.getGradePoint();
                totalCredits += credits;
            }
        }
        return totalCredits == 0 ? 0.0 : round(weightedSum / totalCredits);
    }

    private static double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
