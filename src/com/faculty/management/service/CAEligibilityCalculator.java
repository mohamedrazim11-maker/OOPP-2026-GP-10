package com.faculty.management.service;
import java.util.Map;

// Polymorphism & Concrete Business Logic
public class CAEligibilityCalculator extends EvaluationCalculator {

    @Override
    public double calculateCAPercentage(Map<String, Double> caComponentMarks) {
        if (caComponentMarks == null || caComponentMarks.isEmpty()) {
            return 0.0;
        }

        double totalWeightedScore = 0.0;
        for (Double score : caComponentMarks.values()) {
            totalWeightedScore += score;
        }

        // Return calculated percentage (out of 100)
        return totalWeightedScore;
    }
}

