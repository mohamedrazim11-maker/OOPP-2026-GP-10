package model;

import java.sql.Timestamp;

public class CourseMaterial {
    private int materialId;
    private String courseId;
    private String title;
    private String filePath;
    private Timestamp uploadedDate;

    public CourseMaterial(int materialId, String courseId, String title, String filePath, Timestamp uploadedDate) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.title = title;
        this.filePath = filePath;
        this.uploadedDate = uploadedDate;
    }

    public int getMaterialId() { return materialId; }
    public String getCourseId() { return courseId; }
    public String getTitle() { return title; }
    public String getFilePath() { return filePath; }
    public Timestamp getUploadedDate() { return uploadedDate; }
}
