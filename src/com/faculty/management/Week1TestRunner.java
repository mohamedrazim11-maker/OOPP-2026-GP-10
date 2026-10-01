package com.faculty.management;
import com.faculty.management.model.Mark;
import model.*;
import com.faculty.management.config.DatabaseConnection ;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.service.CAEligibilityCalculator;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Week1TestRunner {
    public static void main(String[] args) {
        System.out.println("=== Starting Week 1 Core Architecture Test ===");

        // 1. Test Inheritance & Encapsulation
        Lecturer lecturer = new Lecturer("LEC001", "Dr. Perera", "perera@fot.sjp.ac.lk", "ICT", "0711234567", "/uploads/perera.jpg");
        System.out.println("Lecturer Created: " + lecturer.getName() + " | Role: " + lecturer.getRole());

        // 2. Test Exception Handling
        try {
            System.out.println("\nTesting Mark Validation (Entering 105)...");
            Mark invalidMark = new Mark("TG001", 1, 105.0);
        } catch (InvalidMarkException e) {
            System.err.println("Caught Expected Exception: " + e.getMessage());
        }

        // 3. Test Abstraction & Polymorphism Logic
        Map<String, Double> sampleCA = new HashMap<>();
        sampleCA.put("Quiz1", 10.0);       // Weighted score
        sampleCA.put("Assessment", 15.0);
        sampleCA.put("MidTerm", 20.0);     // Total CA = 45%

        CAEligibilityCalculator calculator = new CAEligibilityCalculator();
        double caScore = calculator.calculateCAPercentage(sampleCA);
        boolean eligible = calculator.isEligibleForFinalExam(caScore);

        System.out.println("\nCalculated CA: " + caScore + "%");
        System.out.println("CA Eligibility (>= 40%): " + (eligible ? "ELIGIBLE" : "NOT ELIGIBLE"));

        // 4. Test DB Connection
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("\nDatabase Connection Successful!");
            }
        } catch (Exception e) {
            System.err.println("\nDB Connection Failed: " + e.getMessage());
        }

        System.out.println("\n=== Week 1 Code Base Ready for Commit ===");
    }
}
