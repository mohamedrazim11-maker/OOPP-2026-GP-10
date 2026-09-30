package com.faculty.management.model;

import java.time.LocalDate;

/** A medical request created by, and visible only to, its student. */
public class MedicalSubmission {
    private final int medicalId;
    private final String courseCode;
    private final LocalDate startDate, endDate;
    private final String reason, documentPath, status;

    public MedicalSubmission(int medicalId, String courseCode, LocalDate startDate, LocalDate endDate,
                             String reason, String documentPath, String status) {
        this.medicalId = medicalId; this.courseCode = courseCode; this.startDate = startDate; this.endDate = endDate;
        this.reason = reason; this.documentPath = documentPath; this.status = status;
    }
    public int getMedicalId() { return medicalId; }
    public String getCourseCode() { return courseCode; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getDocumentPath() { return documentPath; }
    public String getStatus() { return status; }
}
