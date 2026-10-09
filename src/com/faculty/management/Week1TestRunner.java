package com.faculty.management;

import com.faculty.management.model.*;
import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.service.CAEligibilityCalculator;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Week1TestRunner {
    public static void main(String[] args) {
        System.out.println("=== Starting Week 1 Core Architecture Test ===");

        // 1. Test Inheritance & Encapsulation using leader's Lecturer constructor
        Lecturer lecturer = new Lecturer(
                1,                      // userId (int)
                "drperera",             // username
                "pass123",              // password
                "perera@fot.sjp.ac.lk", // email
                "Dr.",                  // firstName
                "Perera",               // lastName
                null,                   // role (or Role.LECTURER if Role enum exists)
                "0711234567",           // contactNo
                "/uploads/perera.jpg",  // profilePic
                "ACTIVE"                // status
        );

        System.out.println("Lecturer Created: " + lecturer.getFullName() + " | Title: " + lecturer.getRoleTitle());
        System.out.println("Greeting: " + lecturer.getDashboardGreeting());

        // 2. Test Exception Handling
        try {
            System.out.println("\nTesting Mark Validation (Entering 105)...");
            Mark invalidMark = new Mark("TG001", 1, 105.0);
        } catch (InvalidMarkException e) {
            System.err.println("Caught Expected Exception: " + e.getMessage());
        }

        // 3. Test Abstraction & Polymorphism Logic
        Map<String, Double> sampleCA = new HashMap<>();
        sampleCA.put("Quiz1", 10.0);
        sampleCA.put("Assessment", 15.0);
        sampleCA.put("MidTerm", 20.0);

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