package com.faculty.management.service;

import com.faculty.management.dao.MedicalSubmissionDAO;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.MedicalSubmission;
import java.time.LocalDate;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.List;

public class MedicalSubmissionService {
    private static final long MAX_PDF_SIZE_BYTES = 10L * 1024 * 1024;
    private final MedicalSubmissionDAO medicalDAO = new MedicalSubmissionDAO();
    public void submit(int studentId, Integer courseId, LocalDate start, LocalDate end, String reason, String documentPath)
            throws ValidationException, DatabaseException {
        if (start == null || end == null || end.isBefore(start)) throw new ValidationException("Enter a valid medical date range.");
        if (reason == null || reason.trim().length() < 5) throw new ValidationException("Provide a short reason for the medical request.");
        if (reason.trim().length() > 500) throw new ValidationException("Medical reason must be 500 characters or fewer.");
        medicalDAO.create(studentId, courseId, start, end, reason.trim(), documentPath == null ? "" : documentPath.trim());
    }
    public List<MedicalSubmission> getHistory(int studentId) throws DatabaseException { return medicalDAO.findByStudent(studentId); }

    /** Validates and stores a PDF locally so the reviewer can access a stable copy. */
    public String storePdf(int studentId, File source) throws ValidationException {
        if (source == null || !source.isFile()) throw new ValidationException("Select a medical certificate PDF.");
        if (!source.getName().toLowerCase().endsWith(".pdf")) throw new ValidationException("Only PDF medical certificates are accepted.");
        if (source.length() > MAX_PDF_SIZE_BYTES) throw new ValidationException("Medical certificate PDFs must be 10 MB or smaller.");
        try {
            byte[] header = new byte[4];
            try (InputStream input = Files.newInputStream(source.toPath())) {
                if (input.read(header) != 4 || header[0] != '%' || header[1] != 'P' || header[2] != 'D' || header[3] != 'F') {
                    throw new ValidationException("The selected file is not a valid PDF.");
                }
            }
            Path folder = Path.of("uploads", "medical");
            Files.createDirectories(folder);
            Path destination = folder.resolve("medical-" + studentId + "-" + UUID.randomUUID() + ".pdf");
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toString();
        } catch (IOException e) { throw new ValidationException("Could not save the PDF: " + e.getMessage(), e); }
    }
}
