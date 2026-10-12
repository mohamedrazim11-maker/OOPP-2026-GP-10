package com.faculty.management.service;

import com.faculty.management.dao.GradeDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Grade;
import com.faculty.management.util.GPACalculator;

import java.util.List;

/**
 * Business logic for SGPA and CGPA.
 *   SGPA = Σ(Credit × Grade Point) / Σ Credits   — for one semester
 *   CGPA = same formula pooled across every semester the student has grades for
 */
public class GPAService {

    private final GradeService gradeService = new GradeService();
    private final GradeDAO gradeDAO = new GradeDAO();

    /** SGPA for one semester, computing/refreshing that semester's grades first. */
    public double calculateSgpa(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        List<Grade> grades = gradeService.getIndividualGrades(studentId, semesterNumber, academicYear);
        return GPACalculator.computeSgpa(grades);
    }

    /** CGPA across every course the student has a computed Grade for, across all semesters. */
    public double calculateCgpa(int studentId) throws DatabaseException {
        List<Grade> allGrades = gradeDAO.findAllGradesForStudent(studentId);
        return GPACalculator.computeCgpa(allGrades);
    }
}
