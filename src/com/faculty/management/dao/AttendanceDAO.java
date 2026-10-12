package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.AttendanceRecord;
import com.faculty.management.model.Course;
import java.sql.*;
import java.util.*;

public class AttendanceDAO {
    public List<AttendanceRecord> findByStudent(int studentId) throws DatabaseException {
        String sql = "SELECT c.*, COALESCE(a.theory_attended,0) theory_attended, COALESCE(a.theory_total,0) theory_total, " +
                "COALESCE(a.practical_attended,0) practical_attended, COALESCE(a.practical_total,0) practical_total " +
                "FROM enrollments e JOIN courses c ON c.course_id=e.course_id LEFT JOIN attendance_records a " +
                "ON a.student_id=e.student_id AND a.course_id=e.course_id WHERE e.student_id=? ORDER BY c.semester_number, c.course_code";
        List<AttendanceRecord> records = new ArrayList<>();
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) { while (rs.next()) {
                Course course = new Course(rs.getInt("course_id"), rs.getString("course_code"), rs.getString("course_name"),
                        rs.getInt("credits"), rs.getInt("theory_sessions"), rs.getInt("practical_sessions"),
                        rs.getInt("semester_number"), rs.getString("academic_year"), rs.getString("department"));
                records.add(new AttendanceRecord(course, rs.getInt("theory_attended"), rs.getInt("theory_total"),
                        rs.getInt("practical_attended"), rs.getInt("practical_total")));
            }}
        } catch (SQLException e) { throw new DatabaseException("Failed to load attendance: " + e.getMessage(), e); }
        return records;
    }
}
