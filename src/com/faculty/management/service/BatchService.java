package com.faculty.management.service;

import com.faculty.management.dao.BatchDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Batch;
import com.faculty.management.model.Undergraduate;

import java.util.List;

/**
 * Service layer for undergraduate Batch operations.
 * Demonstrates:
 * - Abstraction & Encapsulation: Business logic and data validation separated from DAO and UI.
 * - Error & Exception Handling: Validates business rules and throws custom ValidationExceptions.
 */
public class BatchService {

    private final BatchDAO batchDAO;

    public BatchService() {
        this(new BatchDAO());
    }

    public BatchService(BatchDAO batchDAO) {
        this.batchDAO = (batchDAO != null) ? batchDAO : new BatchDAO();
    }

    public List<Batch> getAllBatches() throws DatabaseException {
        return batchDAO.getAllBatches();
    }

    public List<String> getAvailableBatchNames() {
        return batchDAO.getAvailableBatchNames();
    }

    public Batch getBatchById(int batchId) throws DatabaseException {
        if (batchId <= 0) {
            return null;
        }
        return batchDAO.findById(batchId);
    }

    public Batch createBatch(Batch batch) throws ValidationException, DatabaseException {
        validateBatch(batch);

        if (batchDAO.existsByName(batch.getBatchName())) {
            throw new ValidationException("A batch with the name '" + batch.getBatchName() + "' already exists.");
        }

        return batchDAO.createBatch(batch);
    }

    public boolean updateBatch(Batch batch) throws ValidationException, DatabaseException {
        if (batch == null || batch.getBatchId() <= 0) {
            throw new ValidationException("Invalid batch ID for update.");
        }
        validateBatch(batch);

        if (batchDAO.existsByNameAndNotId(batch.getBatchName(), batch.getBatchId())) {
            throw new ValidationException("Another batch with the name '" + batch.getBatchName() + "' already exists.");
        }

        return batchDAO.updateBatch(batch);
    }

    public boolean deleteBatch(int batchId) throws ValidationException, DatabaseException {
        if (batchId <= 0) {
            throw new ValidationException("Invalid batch ID for deletion.");
        }
        return batchDAO.deleteBatch(batchId);
    }

    public List<Undergraduate> getUndergraduatesInBatch(String batchName) throws DatabaseException {
        if (batchName == null || batchName.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return batchDAO.getUndergraduatesInBatch(batchName.trim());
    }

    private void validateBatch(Batch batch) throws ValidationException {
        if (batch == null) {
            throw new ValidationException("Batch cannot be null.");
        }
        if (batch.getBatchName() == null || batch.getBatchName().trim().isEmpty()) {
            throw new ValidationException("Batch name is required (e.g. '2025/2026').");
        }
        String name = batch.getBatchName().trim();
        if (name.length() < 3 || name.length() > 50) {
            throw new ValidationException("Batch name must be between 3 and 50 characters.");
        }

        if (batch.getAcademicYear() == null || batch.getAcademicYear().trim().isEmpty()) {
            batch.setAcademicYear(name);
        }

        String status = batch.getStatus();
        if (status == null || status.trim().isEmpty()) {
            batch.setStatus("ACTIVE");
        } else {
            status = status.trim().toUpperCase();
            if (!status.equals("ACTIVE") && !status.equals("UPCOMING") && !status.equals("COMPLETED")) {
                throw new ValidationException("Status must be ACTIVE, UPCOMING, or COMPLETED.");
            }
            batch.setStatus(status);
        }
    }
}
