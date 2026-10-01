package com.faculty.management.service;

import com.faculty.management.dao.EnrollmentDAO;
import com.faculty.management.dao.UndergraduateDAO;
import com.faculty.management.exception.BusinessRuleException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Enrollment;
import com.faculty.management.model.Undergraduate;
import com.faculty.management.util.ValidationUtil;

import java.util.List;

/**
 * Business logic for Undergraduate profile and course-detail viewing.
 * Enforces the rule that a student may only self-update contact details
 * and their profile picture — never username, password, or academic data.
 */
public class UndergraduateService {

    private final UndergraduateDAO undergraduateDAO = new UndergraduateDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public Undergraduate getProfile(int studentId) throws DatabaseException, BusinessRuleException {
        Undergraduate student = undergraduateDAO.findById(studentId);
        if (student == null) {
            throw new BusinessRuleException("No student profile found for id " + studentId);
        }
        return student;
    }

    public List<Undergraduate> getAllStudents() throws DatabaseException {
        return undergraduateDAO.findAll();
    }

    /**
     * Updates only the contact number and profile picture for a student,
     * after validating the input. Throws ValidationException for bad
     * input rather than silently accepting it.
     */
    public void updateOwnProfile(int studentId, String contactNo, String profilePicPath) throws ValidationException, DatabaseException {
        if (contactNo == null || contactNo.trim().isEmpty()) {
            throw new ValidationException("Contact number cannot be empty.");
        }
        if (!ValidationUtil.isValidPhoneNumber(contactNo.trim())) {
            throw new ValidationException("Contact number format is invalid. Use digits only (e.g. 0771234567).");
        }
        undergraduateDAO.updateContactAndPicture(studentId, contactNo.trim(), profilePicPath);
    }

    /** All courses a student is/was enrolled in, most recent semester first. */
    public List<Enrollment> getCourseDetails(int studentId) throws DatabaseException {
        return enrollmentDAO.findByStudent(studentId);
    }
}
