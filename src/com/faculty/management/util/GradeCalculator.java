package com.faculty.management.util;

/**
 * Converts final course marks into a Letter Grade and Grade Point Value,
 * per UGC (Sri Lanka) Commission Circular No. 12-2024 — "Guidance of the
 * Grading system of Degree programmes offered under Technology Stream".
 *
 * NOTE: The published circular (UGC ref. 2548) is a scanned/non-text PDF,
 * so it could not be machine-read while building this module. The table
 * below uses the common UGC Sri Lanka Grade / Grade-Point-Value scale that
 * the Technology stream circular is based on (the same scale used across
 * the university system, e.g. Commission Circular No. 901). Please check
 * the official circular against this table before final submission, and
 * adjust the GRADE_BANDS array below if your faculty's cut-offs differ —
 * everything else in Member 4's module (SGPA/CGPA/Results) will keep
 * working unchanged since it only depends on this one table.
 *
 * Demonstrates Abstraction: calling code only needs marksToGrade(); the
 * banding rules are fully hidden.
 */
public class GradeCalculator {

    /**
     * CA vs. Final Examination weighting used to compute the final total
     * mark for a course. Documented assumption — adjust if your course
     * coordinators specify a different split.
     */
    public static final double CA_WEIGHT = 0.40;
    public static final double FINAL_EXAM_WEIGHT = 0.60;

    /** Minimum CA% required to be eligible to sit the final examination. */
    public static final double CA_ELIGIBILITY_THRESHOLD = 40.0;

    private static final GradeBand[] GRADE_BANDS = {
            new GradeBand(90, 100, "A+", 4.00),
            new GradeBand(80, 89.99, "A", 4.00),
            new GradeBand(75, 79.99, "A-", 3.70),
            new GradeBand(70, 74.99, "B+", 3.30),
            new GradeBand(65, 69.99, "B", 3.00),
            new GradeBand(60, 64.99, "B-", 2.70),
            new GradeBand(55, 59.99, "C+", 2.30),
            new GradeBand(50, 54.99, "C", 2.00),
            new GradeBand(45, 49.99, "C-", 1.70),
            new GradeBand(40, 44.99, "D+", 1.30),
            new GradeBand(35, 39.99, "D", 1.00),
            new GradeBand(0, 34.99, "E", 0.00)
    };

    private GradeCalculator() {
        // Prevent instantiation
    }

    /**
     * Computes the final total mark for a course from the CA% and the
     * final examination mark using the documented weighting.
     */
    public static double computeFinalTotal(double caMarks, double finalExamMarks) {
        double total = (caMarks * CA_WEIGHT) + (finalExamMarks * FINAL_EXAM_WEIGHT);
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * A student is eligible to sit the final examination (and therefore to
     * be graded) only if their CA percentage is &gt;= 40%.
     */
    public static boolean isCaEligible(double caMarks) {
        return caMarks >= CA_ELIGIBILITY_THRESHOLD;
    }

    /**
     * Converts a mark out of 100 into its Letter Grade under the scale
     * above. Marks outside [0, 100] are clamped.
     */
    public static String marksToGradeLetter(double marks) {
        return bandFor(marks).letter;
    }

    /**
     * Converts a mark out of 100 into its Grade Point Value under the
     * scale above.
     */
    public static double marksToGradePoint(double marks) {
        return bandFor(marks).gradePoint;
    }

    private static GradeBand bandFor(double marks) {
        double clamped = Math.max(0, Math.min(100, marks));
        for (GradeBand band : GRADE_BANDS) {
            if (clamped >= band.min && clamped <= band.max) {
                return band;
            }
        }
        return GRADE_BANDS[GRADE_BANDS.length - 1];
    }

    private static class GradeBand {
        final double min;
        final double max;
        final String letter;
        final double gradePoint;

        GradeBand(double min, double max, String letter, double gradePoint) {
            this.min = min;
            this.max = max;
            this.letter = letter;
            this.gradePoint = gradePoint;
        }
    }
}
