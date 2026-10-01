package com.faculty.management.service;

import com.faculty.management.dao.AttendanceDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.AttendanceRecord;
import java.util.List;

public class AttendanceService {
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    public List<AttendanceRecord> getStudentAttendance(int studentId) throws DatabaseException {
        return attendanceDAO.findByStudent(studentId);
    }
}
