package com.faculty.management.service;

import com.faculty.management.dao.LecturerDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Lecturer;

import java.sql.SQLException;

public class LecturerService {
    private final LecturerDAO lecturerDAO = new LecturerDAO();

    public boolean updateProfile(Lecturer lecturer) throws SQLException {
        try {
            return lecturerDAO.updateLecturerProfile(lecturer);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public Lecturer getProfile(int userId) throws SQLException {
        try {
            return lecturerDAO.getLecturerById(userId);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}