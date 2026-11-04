package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;
import com.faculty.management.model.TimetableEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the academic Timetable.
 * Admin (Member 1) creates/maintains entries; Member 3 and Member 4 consume
 * the queries for scheduling and student viewing.
 */
public class TimetableDAO {

    private static final String BASE_SELECT =
            "SELECT t.timetable_id, t.day_of_week, t.start_time, t.end_time, t.session_type, " +
            "t.lecturer_name, t.venue, t.semester_number, t.academic_year, " +
            "c.course_id, c.course_code, c.course_name, c.credits, c.theory_sessions, " +
            "c.practical_sessions, c.department " +
            "FROM timetable t JOIN courses c ON t.course_id = c.course_id ";

    private static final String ORDER_CLAUSE =
            " ORDER BY CASE t.day_of_week " +
            "WHEN 'MONDAY' THEN 1 WHEN 'TUESDAY' THEN 2 WHEN 'WEDNESDAY' THEN 3 " +
            "WHEN 'THURSDAY' THEN 4 WHEN 'FRIDAY' THEN 5 WHEN 'SATURDAY' THEN 6 ELSE 7 END, t.start_time";

    /**
     * Retrieves all timetable entries ordered by day and start time.
     */
    public List<TimetableEntry> findAll() throws DatabaseException {
        String sql = BASE_SELECT + ORDER_CLAUSE;
        List<TimetableEntry> entries = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                entries.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all timetable entries: " + e.getMessage(), e);
        }
        return entries;
    }

    /**
     * Retrieves a single timetable entry by its ID.
     */
    public TimetableEntry findById(int timetableId) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE t.timetable_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timetableId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch timetable entry #" + timetableId + ": " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Full timetable for a given semester/year, ordered for display (Day | Time).
     */
    public List<TimetableEntry> findBySemester(int semesterNumber, String academicYear) throws DatabaseException {
        String sql = BASE_SELECT + "WHERE t.semester_number = ? AND t.academic_year = ?" + ORDER_CLAUSE;
        return query(sql, semesterNumber, academicYear, -1);
    }

    /**
     * Timetable filtered to only the courses a specific student is enrolled in for that semester.
     */
    public List<TimetableEntry> findForStudent(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        String sql = BASE_SELECT +
                "JOIN enrollments e ON e.course_id = c.course_id AND e.student_id = ? " +
                "WHERE t.semester_number = ? AND t.academic_year = ?" + ORDER_CLAUSE;
        return query(sql, semesterNumber, academicYear, studentId);
    }

    /**
     * Inserts a new timetable entry into the database.
     */
    public boolean create(TimetableEntry entry) throws DatabaseException {
        String sql = "INSERT INTO timetable (course_id, day_of_week, start_time, end_time, session_type, " +
                     "lecturer_name, venue, semester_number, academic_year) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entry.getCourse().getCourseId());
            stmt.setString(2, entry.getDayOfWeek());
            stmt.setTime(3, entry.getStartTime() != null ? Time.valueOf(entry.getStartTime()) : null);
            stmt.setTime(4, entry.getEndTime() != null ? Time.valueOf(entry.getEndTime()) : null);
            stmt.setString(5, entry.getSessionType());
            stmt.setString(6, entry.getLecturerName());
            stmt.setString(7, entry.getVenue());
            stmt.setInt(8, entry.getSemesterNumber());
            stmt.setString(9, entry.getAcademicYear());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        entry.setTimetableId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create timetable slot: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing timetable entry in the database.
     */
    public boolean update(TimetableEntry entry) throws DatabaseException {
        String sql = "UPDATE timetable SET course_id = ?, day_of_week = ?, start_time = ?, end_time = ?, " +
                     "session_type = ?, lecturer_name = ?, venue = ?, semester_number = ?, academic_year = ? " +
                     "WHERE timetable_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entry.getCourse().getCourseId());
            stmt.setString(2, entry.getDayOfWeek());
            stmt.setTime(3, entry.getStartTime() != null ? Time.valueOf(entry.getStartTime()) : null);
            stmt.setTime(4, entry.getEndTime() != null ? Time.valueOf(entry.getEndTime()) : null);
            stmt.setString(5, entry.getSessionType());
            stmt.setString(6, entry.getLecturerName());
            stmt.setString(7, entry.getVenue());
            stmt.setInt(8, entry.getSemesterNumber());
            stmt.setString(9, entry.getAcademicYear());
            stmt.setInt(10, entry.getTimetableId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update timetable slot #" + entry.getTimetableId() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a timetable entry from the database.
     */
    public boolean delete(int timetableId) throws DatabaseException {
        String sql = "DELETE FROM timetable WHERE timetable_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timetableId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete timetable slot #" + timetableId + ": " + e.getMessage(), e);
        }
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
