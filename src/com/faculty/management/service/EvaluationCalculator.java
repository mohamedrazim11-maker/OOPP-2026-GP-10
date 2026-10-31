package com.faculty.management.service;

import java.util.Map;

/**
 * Demonstrates Abstraction in Academic Operations.
 * Base contract for calculating Continuous Assessment (CA) and final aggregated course scores.
 */
public abstract class EvaluationCalculator {

    /**
     * Calculates the aggregated Continuous Assessment (CA) percentage out of 100.
     *
     * @param caComponentMarks Map of evaluation component names to marks obtained (each out of 100)
     * @return Aggregated CA percentage (0.0 to 100.0)
     */
    public abstract double calculateCAPercentage(Map<String, Double> caComponentMarks);

    /**
     * Calculates the overall final course mark combining Continuous Assessment (CA) and Final Exam score.
     *
     * @param caPercentage  Calculated Continuous Assessment percentage (0.0 to 100.0)
     * @param finalExamMark Marks scored in the end-semester final exam (0.0 to 100.0)
     * @return Final aggregated course mark out of 100.0
     */
    public abstract double calculateFinalTotal(double caPercentage, double finalExamMark);

    /**
     * Continuous Assessment Eligibility Rule:
     * A student is Eligible for the final examination if and only if CA >= 40%.
     *
     * @param caPercentage Continuous Assessment percentage
     * @return true if Eligible (CA >= 40.0%), false otherwise
     */
    public boolean isEligibleForFinalExam(double caPercentage) {
        return caPercentage >= 40.0;
    }
}
