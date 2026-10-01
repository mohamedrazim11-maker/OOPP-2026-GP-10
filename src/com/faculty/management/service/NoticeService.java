package com.faculty.management.service;

import com.faculty.management.dao.NoticeDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Notice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for retrieving and filtering official faculty notices.
 */
public class NoticeService {

    private final NoticeDAO noticeDAO = new NoticeDAO();

    public List<Notice> getAllNotices() throws DatabaseException {
        return noticeDAO.findAll();
    }

    public List<Notice> getNoticesByCategory(String category) throws DatabaseException {
        if (category == null || category.equalsIgnoreCase("ALL") || category.trim().isEmpty()) {
            return getAllNotices();
        }
        return getAllNotices().stream()
                .filter(n -> n.getCategory().equalsIgnoreCase(category.trim()))
                .collect(Collectors.toList());
    }
}
