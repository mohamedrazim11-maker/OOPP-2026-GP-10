package com.faculty.management.service;

import com.faculty.management.dao.TimetableDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.TimetableEntry;

import java.util.List;

/**
 * Business logic for viewing the academic Timetable
 * (Day | Time | Course | Lecturer | Theory/Practical | Venue).
 * Admin (Member 1) is responsible for creating/maintaining entries;
 * this service provides the read-side used by the Undergraduate dashboard.
 */
public class TimetableService {

    private final TimetableDAO timetableDAO = new TimetableDAO();

    /** Timetable limited to the courses a given student is enrolled in. */
    public List<TimetableEntry> getTimetableForStudent(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        return timetableDAO.findForStudent(studentId, semesterNumber, academicYear);
    }

    /** Full department timetable for a semester (useful for batch/admin views). */
    public List<TimetableEntry> getFullTimetable(int semesterNumber, String academicYear) throws DatabaseException {
        return timetableDAO.findBySemester(semesterNumber, academicYear);
    }
}
