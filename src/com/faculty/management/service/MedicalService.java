package com.faculty.management.service;

import com.faculty.management.dao.MedicalDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Medical;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for managing, validating, and reviewing Medical Certificates.
 * Handles medical submission logging, status approvals/rejections, and history queries.
 *
 * OOP Principles Applied:
 * - Abstraction: Encapsulates SQL updates and medical certificate status changes behind business workflows.
 * - Encapsulation: Enforces valid state transitions (APPROVED, REJECTED, PENDING) and date checks.
 * - Exception Handling: Validates input parameters and throws descriptive ValidationException / DatabaseException.
 */
public class MedicalService {

    private final MedicalDAO medicalDAO;

    public MedicalService() {
        this.medicalDAO = new MedicalDAO();
    }

    public MedicalService(MedicalDAO medicalDAO) {
        this.medicalDAO = medicalDAO != null ? medicalDAO : new MedicalDAO();
    }

    /**
     * Adds a new medical certificate record with validation.
     *
     * @param medical Medical domain object
     * @return true if saved successfully
     * @throws ValidationException If validation fails (missing student ID, course ID, or reason)
     * @throws DatabaseException   If database error occurs
     */
    public boolean addMedical(Medical medical) throws ValidationException, DatabaseException {
        if (medical == null) {
            throw new ValidationException("Medical record cannot be null.");
        }
        if (medical.getStudentId() <= 0) {
            throw new ValidationException("A valid Student ID must be specified.");
        }
        if (medical.getCourseId() == null || medical.getCourseId().trim().isEmpty()) {
            throw new ValidationException("Course Code must be specified.");
        }
        if (medical.getMedicalDate() == null) {
            throw new ValidationException("Medical date must be specified.");
        }
        if (medical.getReason() == null || medical.getReason().trim().length() < 3) {
            throw new ValidationException("Please provide a valid medical justification or doctor reason.");
        }

        try {
            return medicalDAO.addMedical(medical);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save medical certificate: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the status of a medical certificate (APPROVED, REJECTED, PENDING).
     *
     * @param medicalId ID of the medical record
     * @param status    New status ("APPROVED", "REJECTED", "PENDING")
     * @return true if updated successfully
     * @throws ValidationException If status is invalid
     * @throws DatabaseException   If database error occurs
     */
    public boolean updateMedicalStatus(int medicalId, String status) throws ValidationException, DatabaseException {
        if (medicalId <= 0) {
            throw new ValidationException("Invalid Medical ID specified.");
        }
        if (status == null) {
            throw new ValidationException("Medical status cannot be null.");
        }
        String upperStatus = status.trim().toUpperCase();
        if (!upperStatus.equals("APPROVED") && !upperStatus.equals("REJECTED") && !upperStatus.equals("PENDING")) {
            throw new ValidationException("Invalid status: " + status + ". Allowed values are APPROVED, REJECTED, or PENDING.");
        }

        try {
            return medicalDAO.updateMedicalStatus(medicalId, upperStatus);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update medical status: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all medical certificates, optionally filtered by status ("ALL", "PENDING", "APPROVED", "REJECTED").
     *
     * @param statusFilter Status filter choice
     * @return List of Medical records
     */
    public List<Medical> getAllMedicals(String statusFilter) {
        return medicalDAO.getAllMedicals(statusFilter);
    }

    /**
     * Retrieves medical records submitted by a specific student.
     */
    public List<Medical> getMedicalsByStudent(int studentId) throws DatabaseException {
        try {
            return medicalDAO.getMedicalsByStudent(studentId);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve medical records: " + e.getMessage(), e);
        }
    }
}
