package com.faculty.management.service;

import com.faculty.management.dao.EnrollmentDAO;
import com.faculty.management.dao.ResultDAO;
import com.faculty.management.exception.BusinessRuleException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Enrollment;
import com.faculty.management.model.Grade;
import com.faculty.management.model.Result;
import com.faculty.management.util.GPACalculator;

import java.util.List;

/**
 * Business logic for Results:
 *   Individual Result: Course | Final Mark | Grade | Grade Point | Credit
 *   Batch Result:       Student | Course | Mark | Grade | GPA
 *
 * A Result is the "published" SGPA snapshot for one student/semester —
 * computing it also (re)computes and saves every underlying course Grade.
 */
public class ResultService {

    private final GradeService gradeService = new GradeService();
    private final ResultDAO resultDAO = new ResultDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    /**
     * Builds (and publishes) the individual Result for one student/semester:
     * recomputes every course Grade, derives SGPA, and stores the summary
     * row in the `results` table.
     */
    public Result publishIndividualResult(int studentId, int semesterNumber, String academicYear) throws DatabaseException, BusinessRuleException {
        List<Enrollment> enrollments = enrollmentDAO.findByStudentAndSemester(studentId, semesterNumber, academicYear);
        if (enrollments.isEmpty()) {
            throw new BusinessRuleException("Student is not enrolled in any course for " + academicYear + ", Semester " + semesterNumber + ".");
        }

        List<Grade> grades = gradeService.computeGradesForSemester(studentId, semesterNumber, academicYear);
        double sgpa = GPACalculator.computeSgpa(grades);

        int creditsAttempted = enrollments.stream().mapToInt(e -> e.getCourse().getCredits()).sum();
        int creditsCompleted = grades.stream().filter(Grade::countsTowardGpa).mapToInt(g -> g.getCourse().getCredits()).sum();

        Result result = new Result(studentId, semesterNumber, academicYear, sgpa, creditsAttempted, creditsCompleted);
        result.setCourseGrades(grades);
        resultDAO.saveOrUpdateResult(result);
        return result;
    }

    /** Every semester's published Result for a student — the source data for the CGPA trend. */
    public List<Result> getResultHistory(int studentId) throws DatabaseException {
        return resultDAO.findAllForStudent(studentId);
    }

    /** Batch Result: every student's published SGPA for one semester. */
    public List<Result> getBatchResults(int semesterNumber, String academicYear) throws DatabaseException {
        return resultDAO.findBatchForSemester(semesterNumber, academicYear);
    }
}
