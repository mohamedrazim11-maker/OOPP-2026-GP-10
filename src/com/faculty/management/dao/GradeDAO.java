package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;
import com.faculty.management.model.Grade;
import com.faculty.management.model.Mark;
import com.faculty.management.util.GradeCalculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for computed course Grades.
 * Reads raw {@link Mark} rows (Member 2's domain), applies
 * {@link GradeCalculator} (Marks -&gt; Grade -&gt; Grade Point), and persists
 * the result into the `grades` table owned by Member 4.
 *
 * Demonstrates Database Handling (JDBC CRUD) and Polymorphism/Abstraction
 * indirectly via GradeCalculator's single conversion entry point being
 * reused for both individual and batch computation below.
 */
public class GradeDAO {

    /**
     * Fetches the raw Mark row for a student in a course, or null if the
     * lecturer has not entered marks for that student yet.
     */
    public Mark findMark(int studentId, int courseId) throws DatabaseException {
        String sql = "SELECT * FROM marks WHERE student_id = ? AND course_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapMark(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch marks: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Computes (or recomputes) the Grade for a student in a course from
     * the raw Mark, and upserts it into the `grades` table.
     *
     * @return the computed Grade, or null if no marks have been entered yet.
     */
    public Grade computeAndSaveGrade(int studentId, Course course, int semesterNumber, String academicYear) throws DatabaseException {
        Mark mark = findMark(studentId, course.getCourseId());
        if (mark == null) {
            return null;
        }

        double caMarks = mark.computeCaMarks();
        double finalExam = mark.getFinalExamOrZero();
        boolean eligible = GradeCalculator.isCaEligible(caMarks);
        double finalTotal = eligible ? GradeCalculator.computeFinalTotal(caMarks, finalExam) : 0.0;
        String letter = eligible ? GradeCalculator.marksToGradeLetter(finalTotal) : "NE";
        double gradePoint = eligible ? GradeCalculator.marksToGradePoint(finalTotal) : 0.0;

        Grade grade = new Grade(studentId, course, semesterNumber, academicYear,
                caMarks, finalExam, finalTotal, eligible, letter, gradePoint);

        upsert(grade);
        return grade;
    }

    private void upsert(Grade grade) throws DatabaseException {
        String sql = "INSERT INTO grades (student_id, course_id, semester_number, academic_year, ca_marks, " +
                     "final_exam_marks, final_total_marks, ca_eligible, grade_letter, grade_point) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE ca_marks = VALUES(ca_marks), final_exam_marks = VALUES(final_exam_marks), " +
                     "final_total_marks = VALUES(final_total_marks), ca_eligible = VALUES(ca_eligible), " +
                     "grade_letter = VALUES(grade_letter), grade_point = VALUES(grade_point)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grade.getStudentId());
            stmt.setInt(2, grade.getCourse().getCourseId());
            stmt.setInt(3, grade.getSemesterNumber());
            stmt.setString(4, grade.getAcademicYear());
            stmt.setDouble(5, grade.getCaMarks());
            stmt.setDouble(6, grade.getFinalExamMarks());
            stmt.setDouble(7, grade.getFinalTotalMarks());
            stmt.setBoolean(8, grade.isCaEligible());
            stmt.setString(9, grade.getGradeLetter());
            stmt.setDouble(10, grade.getGradePoint());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save computed grade: " + e.getMessage(), e);
        }
    }

    /**
     * Every persisted Grade a student has, across all semesters — used by
     * GPAService to compute CGPA (pools grades from every semester, unlike
     * SGPA which only looks at one semester's grades).
     */
    public List<Grade> findAllGradesForStudent(int studentId) throws DatabaseException {
        String sql = "SELECT g.*, c.course_id AS cc_id, c.course_code, c.course_name, c.credits, " +
                     "c.theory_sessions, c.practical_sessions, c.department " +
                     "FROM grades g JOIN courses c ON g.course_id = c.course_id " +
                     "WHERE g.student_id = ? ORDER BY g.academic_year, g.semester_number, c.course_code";
        List<Grade> grades = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("cc_id"), rs.getString("course_code"), rs.getString("course_name"),
                            rs.getInt("credits"), rs.getInt("theory_sessions"), rs.getInt("practical_sessions"),
                            rs.getInt("semester_number"), rs.getString("academic_year"), rs.getString("department"));
                    grades.add(mapGrade(rs, course));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch student's grade history: " + e.getMessage(), e);
        }
        return grades;
    }

    /**
     * Batch view: every student's Grade for one course, keyed by student_id,
     * pulling names from the `users` table for display.
     */
    public Map<Integer, Grade> findBatchGradesForCourse(Course course) throws DatabaseException {
        String sql = "SELECT g.*, u.first_name, u.last_name, u.username FROM grades g " +
                     "JOIN users u ON g.student_id = u.user_id " +
                     "WHERE g.course_id = ? ORDER BY u.first_name";
        Map<Integer, Grade> results = new LinkedHashMap<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, course.getCourseId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = mapGrade(rs, course);
                    results.put(grade.getStudentId(), grade);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch batch grades: " + e.getMessage(), e);
        }
        return results;
    }

    private Mark mapMark(ResultSet rs) throws SQLException {
        Mark mark = new Mark();
        mark.setMarkId(rs.getInt("mark_id"));
        mark.setStudentId(rs.getInt("student_id"));
        mark.setCourseId(rs.getInt("course_id"));
        mark.setQuizMarks(getNullableDouble(rs, "quiz_marks"));
        mark.setAssignmentMarks(getNullableDouble(rs, "assignment_marks"));
        mark.setMidSemMarks(getNullableDouble(rs, "mid_sem_marks"));
        mark.setPracticalMarks(getNullableDouble(rs, "practical_marks"));
        mark.setFinalExamMarks(getNullableDouble(rs, "final_exam_marks"));
        return mark;
    }

    private Grade mapGrade(ResultSet rs, Course course) throws SQLException {
        Grade grade = new Grade(
                rs.getInt("student_id"), course, rs.getInt("semester_number"), rs.getString("academic_year"),
                rs.getDouble("ca_marks"), rs.getDouble("final_exam_marks"), rs.getDouble("final_total_marks"),
                rs.getBoolean("ca_eligible"), rs.getString("grade_letter"), rs.getDouble("grade_point"));
        grade.setGradeId(rs.getInt("grade_id"));
        return grade;
    }

    private Double getNullableDouble(ResultSet rs, String column) throws SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }
}
