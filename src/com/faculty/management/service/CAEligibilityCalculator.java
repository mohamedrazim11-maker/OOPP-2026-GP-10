package com.faculty.management.service;
<<<<<<< HEAD

import java.util.Map;

/**
 * Concrete CA Eligibility Calculator demonstrating Polymorphism.
 * Implements standard Continuous Assessment calculation (average of submitted components)
 * and verifies CA >= 40% threshold for final exam eligibility.
 */
=======
import java.util.Map;

// Polymorphism & Concrete Business Logic
>>>>>>> Aysha
public class CAEligibilityCalculator extends EvaluationCalculator {

    @Override
    public double calculateCAPercentage(Map<String, Double> caComponentMarks) {
        if (caComponentMarks == null || caComponentMarks.isEmpty()) {
            return 0.0;
        }

<<<<<<< HEAD
        double sum = 0.0;
        int count = 0;
        for (Double score : caComponentMarks.values()) {
            if (score != null) {
                sum += score;
                count++;
            }
        }

        // Return calculated percentage (out of 100)
        return count == 0 ? 0.0 : (sum / count);
    }

    @Override
    public double calculateFinalTotal(double caPercentage, double finalExamMark) {
        // Standard formula: 40% Continuous Assessment + 60% Final Examination
        return (caPercentage * 0.40) + (finalExamMark * 0.60);
    }
}
=======
        double totalWeightedScore = 0.0;
        for (Double score : caComponentMarks.values()) {
            totalWeightedScore += score;
        }

        // Return calculated percentage (out of 100)
        return totalWeightedScore;
    }
}

>>>>>>> Aysha
