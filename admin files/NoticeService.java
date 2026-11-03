package com.faculty.management.service;

import com.faculty.management.dao.NoticeDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Notice;
import com.faculty.management.util.ValidationUtil;

import java.util.List;

/**
 * Service layer for managing official faculty notices and circulars.
 * Implements business logic, validation constraints, and database integration.
 * 
 * Demonstrates Core OOP Principles:
 * - Classes and Objects: NoticeService manages notice lifecycle.
 * - Abstraction: Separates presentation controller from direct DAO operations.
 * - Polymorphism: Overloaded method signatures for notice creation, updating, and deletion.
 * - Encapsulation: Encapsulated NoticeDAO dependency and private helper logic.
 * - Error and Exception Handling: Throws ValidationException and DatabaseException with descriptive messages.
 * - Database Handling: Coordinates transactional operations with NoticeDAO.
 */
public class NoticeService {

    private final NoticeDAO noticeDAO;

    public NoticeService() {
        this.noticeDAO = new NoticeDAO();
    }

    public NoticeService(NoticeDAO noticeDAO) {
        this.noticeDAO = (noticeDAO != null) ? noticeDAO : new NoticeDAO();
    }

    /**
     * Retrieves all notices from the database.
     */
    public List<Notice> getAllNotices() throws DatabaseException {
        return noticeDAO.findAll();
    }

    /**
     * Retrieves a single notice by its primary key ID.
     */
    public Notice getNoticeById(int noticeId) throws ValidationException, DatabaseException {
        ValidationUtil.validateNoticeLookup(noticeId);
        Notice notice = noticeDAO.findById(noticeId);
        if (notice == null) {
            throw new ValidationException("Notice with ID " + noticeId + " was not found.");
        }
        return notice;
    }

    /**
     * Retrieves a single notice by exact or keyword title.
     */
    public Notice getNoticeByTitle(String title) throws ValidationException, DatabaseException {
        ValidationUtil.validateNoticeLookup(title);
        Notice notice = noticeDAO.findByTitle(title);
        if (notice == null) {
            throw new ValidationException("Notice with title '" + title + "' was not found.");
        }
        return notice;
    }

    /**
     * Retrieves notices filtered by category.
     */
    public List<Notice> getNoticesByCategory(String category) throws DatabaseException {
        if (category == null || "ALL".equalsIgnoreCase(category.trim()) || category.trim().isEmpty()) {
            return getAllNotices();
        }
        return noticeDAO.findByCategory(category);
    }

    /**
     * Searches notices by keyword matching title, content, or category.
     */
    public List<Notice> searchNotices(String keyword) throws DatabaseException {
        return noticeDAO.search(keyword);
    }

    /**
     * Creates a new notice in the database.
     *
     * @param notice Notice entity to save
     * @return Saved Notice entity with generated ID
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public Notice createNotice(Notice notice) throws ValidationException, DatabaseException {
        ValidationUtil.validateNotice(notice);
        if (noticeDAO.hasNoticeWithTitle(notice.getTitle())) {
            throw new ValidationException("A notice with title '" + notice.getTitle() + "' already exists. Please specify a unique title.");
        }
        return noticeDAO.insert(notice);
    }

    /**
     * Polymorphic overload to create a notice using individual parameters.
     */
    public Notice createNotice(String title, String content, String category, String priority, String postedBy)
            throws ValidationException, DatabaseException {
        Notice notice = new Notice(title, content, category, priority, postedBy);
        return createNotice(notice);
    }

    /**
     * Polymorphic overload to create a notice with default authority attribution.
     */
    public Notice createNotice(String title, String content, String category, String priority)
            throws ValidationException, DatabaseException {
        return createNotice(title, content, category, priority, "Faculty Administration");
    }

    /**
     * Updates an existing notice in the database.
     *
     * @param notice Notice entity containing updated fields
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean updateNotice(Notice notice) throws ValidationException, DatabaseException {
        ValidationUtil.validateNoticeUpdate(notice);
        if (noticeDAO.hasNoticeWithTitle(notice.getTitle(), notice.getNoticeId())) {
            throw new ValidationException("Another notice with title '" + notice.getTitle() + "' already exists. Please specify a unique title.");
        }
        return noticeDAO.update(notice);
    }

    /**
     * Polymorphic overload to update a notice using individual parameters.
     */
    public boolean updateNotice(int noticeId, String title, String content, String category, String priority, String postedBy)
            throws ValidationException, DatabaseException {
        Notice notice = new Notice(noticeId, title, content, category, priority, postedBy, null);
        return updateNotice(notice);
    }

    /**
     * Polymorphic overload to update an existing Notice entity with new title and content.
     */
    public boolean updateNotice(Notice existingNotice, String newTitle, String newContent)
            throws ValidationException, DatabaseException {
        if (existingNotice == null) {
            throw new ValidationException("Existing notice cannot be null.");
        }
        existingNotice.setTitle(newTitle);
        existingNotice.setContent(newContent);
        return updateNotice(existingNotice);
    }

    /**
     * Deletes a notice by its ID.
     *
     * @param noticeId Notice ID
     * @return true if deleted
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean deleteNotice(int noticeId) throws ValidationException, DatabaseException {
        ValidationUtil.validateNoticeDeletion(noticeId);
        return noticeDAO.delete(noticeId);
    }

    /**
     * Polymorphic overload to delete a notice using a Notice entity.
     */
    public boolean deleteNotice(Notice notice) throws ValidationException, DatabaseException {
        if (notice == null) {
            throw new ValidationException("Notice entity cannot be null for deletion.");
        }
        return deleteNotice(notice.getNoticeId());
    }

    /**
     * Polymorphic overload to delete a notice by title string.
     */
    public boolean deleteNotice(String title) throws ValidationException, DatabaseException {
        ValidationUtil.validateNoticeDeletion(title);
        return noticeDAO.deleteByTitle(title);
    }

    /**
     * Returns total count of notices in the system.
     */
    public int getNoticeCount() throws DatabaseException {
        return noticeDAO.getCount();
    }
}
