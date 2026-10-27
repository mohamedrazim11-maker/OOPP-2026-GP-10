package com.faculty.management.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Notice model representing official faculty announcements,
 * circulars, timetable updates, and examination guidelines.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes and Objects: Blueprint for notices within the Faculty Management System.
 * - Encapsulation: Private member variables with controlled public accessors and mutators.
 */
public class Notice {

    private int noticeId;
    private String title;
    private String content;
    private String category; // e.g. "EXAMINATION", "ACADEMIC", "TIMETABLE", "GENERAL"
    private String priority; // e.g. "URGENT", "NORMAL", "INFO"
    private String postedBy; // e.g. "Dean's Office", "Examination Division", "Department of ICT"
    private LocalDate postedDate;

    /**
     * Default constructor.
     */
    public Notice() {
        this.category = "GENERAL";
        this.priority = "NORMAL";
        this.postedBy = "Faculty Administration";
        this.postedDate = LocalDate.now();
    }

    /**
     * Overloaded constructor for creating new notices without explicit ID and date.
     */
    public Notice(String title, String content, String category, String priority, String postedBy) {
        this.title = title;
        this.content = content;
        this.category = (category != null && !category.trim().isEmpty()) ? category : "GENERAL";
        this.priority = (priority != null && !priority.trim().isEmpty()) ? priority : "NORMAL";
        this.postedBy = (postedBy != null && !postedBy.trim().isEmpty()) ? postedBy : "Faculty Administration";
        this.postedDate = LocalDate.now();
    }

    /**
     * Full constructor for notices retrieved from database or storage.
     */
    public Notice(int noticeId, String title, String content, String category, String priority, String postedBy, LocalDate postedDate) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.category = (category != null && !category.trim().isEmpty()) ? category : "GENERAL";
        this.priority = (priority != null && !priority.trim().isEmpty()) ? priority : "NORMAL";
        this.postedBy = (postedBy != null && !postedBy.trim().isEmpty()) ? postedBy : "Faculty Administration";
        this.postedDate = postedDate != null ? postedDate : LocalDate.now();
    }

    // Getters and Setters (Encapsulation)

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    public String getFormattedDate() {
        if (postedDate == null) {
            return "N/A";
        }
        return postedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    @Override
    public String toString() {
        if (title == null || title.trim().isEmpty()) {
            return "Notice #" + noticeId;
        }
        String shortTitle = title.length() > 40 ? title.substring(0, 37) + "..." : title;
        return shortTitle + " [" + (category != null ? category : "GENERAL") + "]";
    }
}
