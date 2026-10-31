package com.faculty.management.service;

import com.faculty.management.dao.EligibilityDAO;
import com.faculty.management.dao.MarkDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.model.Eligibility;
import com.faculty.management.model.Mark;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class EligibilityService {
    private final EligibilityDAO eligibilityDAO = new EligibilityDAO();
    private final MarkDAO markDAO = new MarkDAO();

    public Eligibility calculateAndSaveEligibility(int studentId, int courseId, double totalObtained, double totalMax)
            throws DatabaseException, SQLException {

        if (totalMax <= 0) {
            throw new IllegalArgumentException("Total Max CA Marks must be greater than 0.");
        }

        double caPercentage = (totalObtained / totalMax) * 100.0;
        boolean isEligible = (caPercentage >= 40.0);

        eligibilityDAO.saveOrUpdateEligibility(studentId, courseId, caPercentage, isEligible);

        return new Eligibility(studentId, courseId, caPercentage, isEligible);
    }

    public List<Eligibility> getBatchEligibility(Integer courseFilter, String statusFilter)
            throws DatabaseException, SQLException {

        List<Eligibility> allRecords = eligibilityDAO.getAllEligibilityRecords();

        return allRecords.stream()
                .filter(e -> courseFilter == null || e.getCourseId() == courseFilter)
                .filter(e -> statusFilter == null || statusFilter.equalsIgnoreCase("All") || e.getStatusString().equalsIgnoreCase(statusFilter))
                .collect(Collectors.toList());
    }

    public List<Mark> getStudentMarks(String studentId) throws DatabaseException, SQLException, InvalidMarkException {
        return markDAO.getMarksByStudent(studentId);
    }

    public List<Mark> getStudentMarks(String studentId, String courseId) throws DatabaseException, SQLException, InvalidMarkException {
        try {
            int cId = Integer.parseInt(courseId);
            List<Mark> marks = markDAO.getMarksByStudentAndCourse(studentId, cId);
            if (!marks.isEmpty()) return marks;
        } catch (NumberFormatException ignored) {
        }
        // Fall back to all marks if courseId is invalid or no course-specific marks found
        return markDAO.getMarksByStudent(studentId);
    }
}