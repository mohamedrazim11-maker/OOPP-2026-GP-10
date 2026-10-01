package com.faculty.management.controller;

import com.faculty.management.dao.EnrollmentDAO;
import com.faculty.management.exception.BusinessRuleException;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Enrollment;
import com.faculty.management.model.Grade;
import com.faculty.management.model.Result;
import com.faculty.management.model.TimetableEntry;
import com.faculty.management.model.Undergraduate;
import com.faculty.management.service.GPAService;
import com.faculty.management.service.GradeService;
import com.faculty.management.service.ResultService;
import com.faculty.management.service.TimetableService;
import com.faculty.management.service.UndergraduateService;
import com.faculty.management.service.AttendanceService;
import com.faculty.management.service.MedicalSubmissionService;
import com.faculty.management.model.AttendanceRecord;
import com.faculty.management.model.MedicalSubmission;

import java.time.LocalDate;
import java.io.File;

import java.util.List;

/**
 * Controller layer for Member 4's module — the single entry point the GUI
 * panels talk to. Keeps Swing code free of SQL/business logic and keeps
 * services free of any knowledge of the GUI (clean separation of concerns).
 */
public class UndergraduateController {

    private final UndergraduateService undergraduateService = new UndergraduateService();
    private final TimetableService timetableService = new TimetableService();
    private final GradeService gradeService = new GradeService();
    private final ResultService resultService = new ResultService();
    private final GPAService gpaService = new GPAService();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final AttendanceService attendanceService = new AttendanceService();
    private final MedicalSubmissionService medicalSubmissionService = new MedicalSubmissionService();
    private final com.faculty.management.service.NoticeService noticeService = new com.faculty.management.service.NoticeService();

    public Undergraduate getProfile(int studentId) throws DatabaseException, BusinessRuleException {
        return undergraduateService.getProfile(studentId);
    }

    public void updateOwnProfile(int studentId, String contactNo, String profilePicPath) throws ValidationException, DatabaseException {
        undergraduateService.updateOwnProfile(studentId, contactNo, profilePicPath);
    }

    public List<Enrollment> getCourseDetails(int studentId) throws DatabaseException {
        return undergraduateService.getCourseDetails(studentId);
    }

    /** Distinct [semesterNumber, academicYear] pairs a student has ever been enrolled in, most recent first. */
    public List<String[]> getEnrolledSemesters(int studentId) throws DatabaseException {
        return enrollmentDAO.findDistinctSemestersForStudent(studentId);
    }

    public List<TimetableEntry> getTimetable(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        return timetableService.getTimetableForStudent(studentId, semesterNumber, academicYear);
    }

    public List<Grade> getGrades(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        return gradeService.getIndividualGrades(studentId, semesterNumber, academicYear);
    }

    public double getSgpa(int studentId, int semesterNumber, String academicYear) throws DatabaseException {
        return gpaService.calculateSgpa(studentId, semesterNumber, academicYear);
    }

    public double getCgpa(int studentId) throws DatabaseException {
        return gpaService.calculateCgpa(studentId);
    }

    public Result publishResult(int studentId, int semesterNumber, String academicYear) throws DatabaseException, BusinessRuleException {
        return resultService.publishIndividualResult(studentId, semesterNumber, academicYear);
    }

    public List<Result> getResultHistory(int studentId) throws DatabaseException {
        return resultService.getResultHistory(studentId);
    }

    public List<Result> getBatchResults(int semesterNumber, String academicYear) throws DatabaseException {
        return resultService.getBatchResults(semesterNumber, academicYear);
    }

    public List<AttendanceRecord> getAttendance(int studentId) throws DatabaseException {
        return attendanceService.getStudentAttendance(studentId);
    }

    public void submitMedical(int studentId, Integer courseId, LocalDate start, LocalDate end, String reason, String documentPath)
            throws ValidationException, DatabaseException {
        medicalSubmissionService.submit(studentId, courseId, start, end, reason, documentPath);
    }

    public List<MedicalSubmission> getMedicalHistory(int studentId) throws DatabaseException {
        return medicalSubmissionService.getHistory(studentId);
    }

    public String uploadMedicalPdf(int studentId, File pdf) throws ValidationException {
        return medicalSubmissionService.storePdf(studentId, pdf);
    }

    public List<com.faculty.management.model.Notice> getNotices() throws DatabaseException {
        return noticeService.getAllNotices();
    }

    public List<com.faculty.management.model.Notice> getNoticesByCategory(String category) throws DatabaseException {
        return noticeService.getNoticesByCategory(category);
    }
}
