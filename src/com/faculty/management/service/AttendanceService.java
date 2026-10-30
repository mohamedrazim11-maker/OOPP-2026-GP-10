package com.faculty.management.service;

import com.faculty.management.dao.AttendanceDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Attendance;
import com.faculty.management.model.AttendanceRecord;
import com.faculty.management.model.Undergraduate;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service layer handling Attendance recording, daily session management,
 * batch attendance logging, and student attendance inquiries.
 *
 * OOP Principles Applied:
 * - Abstraction: Encapsulates SQL transactions and attendance table mappings behind clean business methods.
 * - Encapsulation: Private AttendanceDAO dependency; input validations prevent invalid session numbers and dates.
 * - Error Handling: Translates low-level SQLExceptions into typed ValidationException and DatabaseException.
 */
public class AttendanceService {

    private final AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public AttendanceService(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO != null ? attendanceDAO : new AttendanceDAO();
    }

    /**
     * Finds attendance totals for an enrolled student (used by Member 4 Undergraduate portal).
     */
    public List<AttendanceRecord> getStudentAttendance(int studentId) throws DatabaseException {
        return attendanceDAO.findByStudent(studentId);
    }

    /**
     * Finds an existing session or creates a new session in attendance_sessions table.
     */
    public int getOrCreateSession(String courseCode, String sessionType, int sessionNumber, LocalDate date, int createdBy)
            throws ValidationException, DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new ValidationException("Course Code must be specified.");
        }
        if (sessionType == null || (!sessionType.equalsIgnoreCase("THEORY") && !sessionType.equalsIgnoreCase("PRACTICAL"))) {
            throw new ValidationException("Session type must be either 'THEORY' or 'PRACTICAL'.");
        }
        if (sessionNumber <= 0 || sessionNumber > 30) {
            throw new ValidationException("Session number must be between 1 and 30.");
        }
        if (date == null) {
            throw new ValidationException("Session date must be specified.");
        }

        try {
            return attendanceDAO.getOrCreateSession(courseCode.trim().toUpperCase(), sessionType.trim().toUpperCase(), sessionNumber, date, createdBy);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize attendance session: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves marked attendance status for a given session (studentId -> "PRESENT" or "ABSENT").
     */
    public Map<Integer, String> getAttendanceForSession(int sessionId) throws DatabaseException {
        try {
            return attendanceDAO.getAttendanceForSession(sessionId);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch session attendance: " + e.getMessage(), e);
        }
    }

    /**
     * Saves or updates the attendance for a list of students in a session.
     */
    public boolean saveBatchAttendance(int sessionId, Map<Integer, String> studentAttendance)
            throws ValidationException, DatabaseException {
        if (sessionId <= 0) {
            throw new ValidationException("Invalid session ID for attendance submission.");
        }
        if (studentAttendance == null || studentAttendance.isEmpty()) {
            throw new ValidationException("No student attendance records to save.");
        }

        try {
            return attendanceDAO.saveBatchAttendance(sessionId, studentAttendance);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save batch attendance: " + e.getMessage(), e);
        }
    }

    /**
     * Loads all Undergraduate students for the attendance roster.
     */
    public List<Undergraduate> getAllUndergraduates() {
        return attendanceDAO.getAllUndergraduates();
    }

    /**
     * Deletes all attendance records and the session itself for the given session ID.
     */
    public boolean deleteSessionAttendance(int sessionId) throws DatabaseException {
        try {
            return attendanceDAO.deleteSessionAttendance(sessionId);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete session attendance: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if attendance has already been recorded for a specific session ID.
     */
    public boolean hasAttendanceRecords(int sessionId) {
        return attendanceDAO.hasAttendanceRecords(sessionId);
    }

    /**
     * Basic CRUD operations for single attendance records.
     */
    public boolean addAttendance(Attendance attendance) throws DatabaseException {
        try {
            return attendanceDAO.addAttendance(attendance);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add attendance: " + e.getMessage(), e);
        }
    }

    public boolean updateAttendance(Attendance attendance) throws DatabaseException {
        try {
            return attendanceDAO.updateAttendance(attendance);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update attendance: " + e.getMessage(), e);
        }
    }

    public boolean deleteAttendance(int recordId) throws DatabaseException {
        try {
            return attendanceDAO.deleteAttendance(recordId);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete attendance: " + e.getMessage(), e);
        }
    }

    public List<Attendance> getAttendanceBySession(int sessionId) throws DatabaseException {
        try {
            return attendanceDAO.getAttendanceBySession(sessionId);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve session attendance: " + e.getMessage(), e);
        }
    }
}
