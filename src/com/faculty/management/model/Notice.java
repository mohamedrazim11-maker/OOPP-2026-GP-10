package com.faculty.management.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Notice model representing official faculty announcements,
 * circulars, timetable updates, and examination guidelines.
 */
public class Notice {

    private final int noticeId;
    private final String title;
    private final String content;
    private final String category; // e.g. "EXAMINATION", "ACADEMIC", "TIMETABLE", "GENERAL"
    private final String priority; // e.g. "URGENT", "NORMAL", "INFO"
    private final String postedBy; // e.g. "Dean's Office", "Examination Division", "Department of ICT"
    private final LocalDate postedDate;

    public Notice(int noticeId, String title, String content, String category, String priority, String postedBy, LocalDate postedDate) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.priority = priority;
        this.postedBy = postedBy;
        this.postedDate = postedDate != null ? postedDate : LocalDate.now();
    }

    public int getNoticeId() {
        return noticeId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public String getFormattedDate() {
        return postedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
