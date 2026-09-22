package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;
import com.faculty.management.model.TimetableEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the academic Timetable.
 * Admin (Member 1) creates/maintains entries; Member 4 provides the
 * student-facing viewing queries.
 */
public class TimetableDAO {

    private static final String BASE_SELECT =
            "SELECT t.timetable_id, t.day_of_week, t.start_time, t.end_time, t.session_type, " +
            "t.lecturer_name, t.venue, t.semester_number, t.academic_year, " +
            "c.course_id, c.course_code, c.course_name, c.credits, c.theory_sessions, " +
            "c.practical_sessions, c.department " +
            "FROM timetable t JOIN courses c ON t.course_id = c.course_id ";

    /** Full timetable for a given semester/year, ordered for display (Day | Time). */
    public List<TimetableEntry> findBySemester(int semesterNumber, String academicYear) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE t.semester_number = ? AND t.academic_year = ? " +
                     "ORDER BY FIELD(t.day_of_week,'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'), t.start_time";
        return query(sql, semesterNumber, academicYear, -1);
    }

    /** Timetable filtered to only the courses a specific student is enrolled in for that semester. */
    public List<TimetableEntry> findForStudent(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        String sql = BASE_SELECT +
                "JOIN enrollments e ON e.course_id = c.course_id AND e.student_id = ? " +
                "WHERE t.semester_number = ? AND t.academic_year = ? " +
                "ORDER BY FIELD(t.day_of_week,'MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY'), t.start_time";
        return query(sql, semesterNumber, academicYear, studentId);
    }

    private List<TimetableEntry> query(String sql, int semesterNumber, String academicYear, int studentIdOrNegative) throws DatabaseException {
        List<TimetableEntry> entries = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (studentIdOrNegative >= 0) {
                stmt.setInt(1, studentIdOrNegative);
                stmt.setInt(2, semesterNumber);
                stmt.setString(3, academicYear);
            } else {
                stmt.setInt(1, semesterNumber);
                stmt.setString(2, academicYear);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch timetable: " + e.getMessage(), e);
        }
        return entries;
    }

    private TimetableEntry mapRow(ResultSet rs) throws SQLException {
        Course course = new Course(
                rs.getInt("course_id"), rs.getString("course_code"), rs.getString("course_name"),
                rs.getInt("credits"), rs.getInt("theory_sessions"), rs.getInt("practical_sessions"),
                rs.getInt("semester_number"), rs.getString("academic_year"), rs.getString("department"));

        Time start = rs.getTime("start_time");
        Time end = rs.getTime("end_time");

        return new TimetableEntry(
                rs.getInt("timetable_id"), course, rs.getString("day_of_week"),
                start != null ? start.toLocalTime() : null, end != null ? end.toLocalTime() : null,
                rs.getString("session_type"), rs.getString("lecturer_name"), rs.getString("venue"),
                rs.getInt("semester_number"), rs.getString("academic_year"));
    }
}
