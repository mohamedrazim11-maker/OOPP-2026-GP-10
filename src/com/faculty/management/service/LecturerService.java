package com.faculty.management.service;

import com.faculty.management.dao.LecturerDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;
import com.faculty.management.model.Lecturer;

import java.sql.SQLException;
import java.util.List;

public class LecturerService {
    private final LecturerDAO lecturerDAO = new LecturerDAO();

    public boolean updateProfile(Lecturer lecturer) throws SQLException, DatabaseException {
        return lecturerDAO.updateLecturerProfile(lecturer);
    }

    public Lecturer getProfile(int userId) throws SQLException, DatabaseException {
        return lecturerDAO.getLecturerById(userId);
    }

    public List<Course> getAssignedCourses(int lecturerId) throws SQLException, DatabaseException {
        return lecturerDAO.getAssignedCourses(lecturerId);
    }
}