package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;
import com.faculty.management.model.Enrollment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for student Course Enrollments.
 * Owned by Member 4 — drives which courses appear on a student's
 * dashboard, timetable, grade sheet, and SGPA/CGPA calculations.
 */
public class EnrollmentDAO {

    private static final String BASE_SELECT =
            "SELECT e.enrollment_id, e.student_id, e.semester_number, e.academic_year, e.status, " +
            "c.course_id, c.course_code, c.course_name, c.credits, c.theory_sessions, " +
            "c.practical_sessions, c.department " +
            "FROM enrollments e JOIN courses c ON e.course_id = c.course_id ";

    public List<Enrollment> findByStudent(int studentId) throws DatabaseException {
        return query(BASE_SELECT + "WHERE e.student_id = ? ORDER BY e.academic_year DESC, e.semester_number, c.course_code", studentId, null, null);
    }

    public List<Enrollment> findByStudentAndSemester(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        return query(BASE_SELECT + "WHERE e.student_id = ? AND e.semester_number = ? AND e.academic_year = ? ORDER BY c.course_code",
                studentId, semesterNumber, academicYear);
    }

    /** Distinct (semester, academic year) pairs a student has ever been enrolled in, most recent first. */
    public List<String[]> findDistinctSemestersForStudent(int studentId) throws DatabaseException {
        String sql = "SELECT DISTINCT semester_number, academic_year FROM enrollments " +
                     "WHERE student_id = ? ORDER BY academic_year DESC, semester_number DESC";
        List<String[]> semesters = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    semesters.add(new String[]{String.valueOf(rs.getInt("semester_number")), rs.getString("academic_year")});
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch enrolled semesters: " + e.getMessage(), e);
        }
        return semesters;
    }

    private List<Enrollment> query(String sql, Integer studentId, Integer semesterNumber, String academicYear) throws DatabaseException {
        List<Enrollment> enrollments = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            if (studentId != null) stmt.setInt(idx++, studentId);
            if (semesterNumber != null) stmt.setInt(idx++, semesterNumber);
            if (academicYear != null) stmt.setString(idx, academicYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("course_id"), rs.getString("course_code"), rs.getString("course_name"),
                            rs.getInt("credits"), rs.getInt("theory_sessions"), rs.getInt("practical_sessions"),
                            rs.getInt("semester_number"), rs.getString("academic_year"), rs.getString("department"));
                    Enrollment enrollment = new Enrollment(
                            rs.getInt("enrollment_id"), rs.getInt("student_id"), course,
                            rs.getInt("semester_number"), rs.getString("academic_year"), rs.getString("status"));
                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch enrollments: " + e.getMessage(), e);
        }
        return enrollments;
    }
}
