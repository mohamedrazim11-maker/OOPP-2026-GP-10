package com.faculty.management.service;

import java.util.Map;

// Abstraction
public abstract class EvaluationCalculator {
    // Abstract method to calculate final aggregated CA percentage
    public abstract double calculateCAPercentage(Map<String, Double> caComponentMarks);

    // Business Logic Rule: CA >= 40% is Eligible
    public boolean isEligibleForFinalExam(double caPercentage) {
        return caPercentage >= 40.0;
    }
}
