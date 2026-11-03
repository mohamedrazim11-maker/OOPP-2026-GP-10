package com.faculty.management.model;

import java.sql.Timestamp;

<<<<<<< HEAD
/**
 * Represents a course material document uploaded by a lecturer.
 * Demonstrates Classes & Objects and Encapsulation.
 */
public class CourseMaterial {
    private int materialId;
    private String courseId; // Numeric ID or Course Code
    private int uploadedBy;  // Lecturer user_id
=======
public class CourseMaterial {
    private int materialId;
    private String courseId;
>>>>>>> Aysha
    private String title;
    private String filePath;
    private Timestamp uploadedDate;

<<<<<<< HEAD
    public CourseMaterial() {
    }

    public CourseMaterial(int materialId, String courseId, String title, String filePath, Timestamp uploadedDate) {
        this(materialId, courseId, 0, title, filePath, uploadedDate);
    }

    public CourseMaterial(int materialId, String courseId, int uploadedBy, String title, String filePath, Timestamp uploadedDate) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.uploadedBy = uploadedBy;
=======
    public CourseMaterial(int materialId, String courseId, String title, String filePath, Timestamp uploadedDate) {
        this.materialId = materialId;
        this.courseId = courseId;
>>>>>>> Aysha
        this.title = title;
        this.filePath = filePath;
        this.uploadedDate = uploadedDate;
    }

<<<<<<< HEAD
    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(int uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Timestamp getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Timestamp uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    @Override
    public String toString() {
        return title + " (" + courseId + ")";
    }
=======
    public int getMaterialId() { return materialId; }
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getFilePath() { return filePath; }
    public Timestamp getUploadedDate() { return uploadedDate; }
>>>>>>> Aysha
}
