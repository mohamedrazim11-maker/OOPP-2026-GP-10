package com.faculty.management.service;

import java.util.Map;

/**
 * Concrete Final Mark Calculator demonstrating Polymorphism.
 * Implements weighted continuous assessment calculation and practical/theory integrated final score.
 */
public class FinalMarkCalculator extends EvaluationCalculator {

    @Override
    public double calculateCAPercentage(Map<String, Double> caComponentMarks) {
        if (caComponentMarks == null || caComponentMarks.isEmpty()) {
            return 0.0;
        }

        // Weighted continuous assessment: Quizzes 20%, Mid-Sem 30%, Assessments 25%, Practicals 25%
        double totalWeighted = 0.0;
        double totalWeights = 0.0;

        for (Map.Entry<String, Double> entry : caComponentMarks.entrySet()) {
            if (entry.getValue() != null) {
                String key = entry.getKey().toUpperCase();
                double weight = 25.0; // default weight
                if (key.contains("QUIZ")) weight = 20.0;
                else if (key.contains("MID")) weight = 30.0;
                else if (key.contains("PRACTICAL")) weight = 25.0;
                else if (key.contains("ASSESSMENT")) weight = 25.0;

                totalWeighted += (entry.getValue() * (weight / 100.0));
                totalWeights += weight;
            }
        }

        return totalWeights == 0 ? 0.0 : (totalWeighted / (totalWeights / 100.0));
    }

    @Override
    public double calculateFinalTotal(double caPercentage, double finalExamMark) {
        // Balanced 50% Continuous Assessment + 50% Final Examination formula
        return (caPercentage * 0.50) + (finalExamMark * 0.50);
    }
}
