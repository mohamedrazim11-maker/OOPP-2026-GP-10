package com.faculty.management.service;

import com.faculty.management.dao.EnrollmentDAO;
import com.faculty.management.dao.GradeDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Enrollment;
import com.faculty.management.model.Grade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Business logic for course Grade computation and retrieval.
 * Orchestrates: Enrollment (which courses) -&gt; Mark (raw scores, Member 2's
 * data) -&gt; GradeCalculator (Marks -&gt; Grade -&gt; Grade Point) -&gt; persisted Grade.
 *
 * Demonstrates Polymorphism in spirit: the same computeAndSaveGrade() call
 * behaves differently per course depending on whether the student was
 * CA-eligible, without the caller needing to branch on that itself.
 */
public class GradeService {

    private final GradeDAO gradeDAO = new GradeDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    /**
     * Computes (and persists) the Grade for every course a student is
     * enrolled in for a given semester. Courses without marks entered yet
     * are silently skipped (lecturer hasn't uploaded marks) rather than
     * failing the whole batch.
     */
    public List<Grade> computeGradesForSemester(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        List<Enrollment> enrollments = enrollmentDAO.findByStudentAndSemester(studentId, semesterNumber, academicYear);
        List<Grade> grades = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Grade grade = gradeDAO.computeAndSaveGrade(studentId, enrollment.getCourse(), semesterNumber, academicYear);
            if (grade != null) {
                grades.add(grade);
            }
        }
        return grades;
    }

    /** Individual view: Course | Final Mark | Grade | Grade Point | Credit, for one semester. */
    public List<Grade> getIndividualGrades(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        return computeGradesForSemester(studentId, semesterNumber, academicYear);
    }

    /** Batch view: every enrolled student's already-computed Grade for one specific course. */
    public Map<Integer, Grade> getBatchGradesForCourse(com.faculty.management.model.Course course) throws DatabaseException {
        return gradeDAO.findBatchGradesForCourse(course);
    }
}
