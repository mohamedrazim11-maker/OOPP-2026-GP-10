package com.faculty.management.service;

import com.faculty.management.dao.TimetableDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.TimetableEntry;

import java.util.List;

/**
 * Business logic service for the academic Timetable
 * (Day | Time | Course | Lecturer | Theory/Practical | Venue | Semester).
 * Admin (Member 1) creates and maintains timetable entries;
 * Member 3 and Member 4 consume this service for viewing and scheduling.
 */
public class TimetableService {

    private final TimetableDAO timetableDAO;

    public TimetableService() {
        this.timetableDAO = new TimetableDAO();
    }

    public TimetableService(TimetableDAO timetableDAO) {
        this.timetableDAO = (timetableDAO != null) ? timetableDAO : new TimetableDAO();
    }

    public List<TimetableEntry> getAllTimetableEntries() throws DatabaseException {
        return timetableDAO.findAll();
    }

    public TimetableEntry getTimetableById(int timetableId) throws DatabaseException {
        return timetableDAO.findById(timetableId);
    }

    /** Timetable limited to the courses a given student is enrolled in. */
    public List<TimetableEntry> getTimetableForStudent(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        return timetableDAO.findForStudent(studentId, semesterNumber, academicYear);
    }

    /** Full department timetable for a semester (useful for batch/admin views). */
    public List<TimetableEntry> getFullTimetable(int semesterNumber, String academicYear) throws DatabaseException {
        return timetableDAO.findBySemester(semesterNumber, academicYear);
    }

    public boolean createTimetableEntry(TimetableEntry entry) throws ValidationException, DatabaseException {
        validateTimetableEntry(entry);
        return timetableDAO.create(entry);
    }

    public boolean updateTimetableEntry(TimetableEntry entry) throws ValidationException, DatabaseException {
        if (entry.getTimetableId() <= 0) {
            throw new ValidationException("Invalid timetable slot ID for update.");
        }
        validateTimetableEntry(entry);
        return timetableDAO.update(entry);
    }

    public boolean deleteTimetableEntry(int timetableId) throws DatabaseException {
        if (timetableId <= 0) {
            throw new DatabaseException("Invalid timetable slot ID for deletion.");
        }
        return timetableDAO.delete(timetableId);
    }

    private void validateTimetableEntry(TimetableEntry entry) throws ValidationException {
        if (entry == null) {
            throw new ValidationException("Timetable entry cannot be null.");
        }
        if (entry.getCourse() == null || entry.getCourse().getCourseId() <= 0) {
            throw new ValidationException("Course module selection is required.");
        }
        if (entry.getDayOfWeek() == null || entry.getDayOfWeek().trim().isEmpty()) {
            throw new ValidationException("Day of week is required.");
        }
        if (entry.getStartTime() == null) {
            throw new ValidationException("Start time is required.");
        }
        if (entry.getEndTime() == null) {
            throw new ValidationException("End time is required.");
        }
        if (!entry.getEndTime().isAfter(entry.getStartTime())) {
            throw new ValidationException("End time must be later than start time.");
        }
        if (entry.getSessionType() == null || entry.getSessionType().trim().isEmpty()) {
            throw new ValidationException("Session type (THEORY or PRACTICAL) is required.");
        }
        if (entry.getVenue() == null || entry.getVenue().trim().isEmpty()) {
            throw new ValidationException("Venue or laboratory room is required.");
        }
        if (entry.getSemesterNumber() < 1 || entry.getSemesterNumber() > 8) {
            throw new ValidationException("Semester must be between 1 and 8.");
        }
        if (entry.getAcademicYear() == null || entry.getAcademicYear().trim().isEmpty()) {
            throw new ValidationException("Academic year is required (e.g., 2025/2026).");
        }
    }
}
