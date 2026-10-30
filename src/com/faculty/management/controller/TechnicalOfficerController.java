package com.faculty.management.controller;

import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Course;
import com.faculty.management.model.Medical;
import com.faculty.management.model.Notice;
import com.faculty.management.model.StudentAttendanceSummary;
import com.faculty.management.model.TimetableEntry;
import com.faculty.management.model.Undergraduate;
import com.faculty.management.model.User;
import com.faculty.management.service.AttendanceEligibilityService;
import com.faculty.management.service.AttendanceService;
import com.faculty.management.service.CourseService;
import com.faculty.management.service.MedicalService;
import com.faculty.management.service.NoticeService;
import com.faculty.management.service.TimetableService;
import com.faculty.management.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller layer for Member 3 (Technical Officer - Attendance & Medical Module).
 * Serves as the single unified entry point that the GUI dialogs interact with.
 *
 * Architecture Compliance:
 * Keeps Swing GUI code completely decoupled from direct SQL/DAO operations.
 * Flow: GUI Dialogs -> TechnicalOfficerController -> Services -> DAOs -> MySQL Database.
 *
 * OOP Principles Applied:
 * - Abstraction: Exposes clean, high-level business capabilities to GUI dialogs.
 * - Encapsulation: Coordinates multiple backend services while keeping service implementations private.
 * - Separation of Concerns: GUI delegates user intent here; controller delegates business logic to services.
 */
public class TechnicalOfficerController {

    private final AttendanceService attendanceService;
    private final AttendanceEligibilityService eligibilityService;
    private final MedicalService medicalService;
    private final CourseService courseService;
    private final TimetableService timetableService;
    private final NoticeService noticeService;
    private final UserService userService;

    public TechnicalOfficerController() {
        this.attendanceService = new AttendanceService();
        this.eligibilityService = new AttendanceEligibilityService();
        this.medicalService = new MedicalService();
        this.courseService = new CourseService();
        this.timetableService = new TimetableService();
        this.noticeService = new NoticeService();
        this.userService = new UserService();
    }

    public TechnicalOfficerController(AttendanceService attendanceService,
                                      AttendanceEligibilityService eligibilityService,
                                      MedicalService medicalService,
                                      CourseService courseService,
                                      TimetableService timetableService,
                                      NoticeService noticeService,
                                      UserService userService) {
        this.attendanceService = attendanceService != null ? attendanceService : new AttendanceService();
        this.eligibilityService = eligibilityService != null ? eligibilityService : new AttendanceEligibilityService();
        this.medicalService = medicalService != null ? medicalService : new MedicalService();
        this.courseService = courseService != null ? courseService : new CourseService();
        this.timetableService = timetableService != null ? timetableService : new TimetableService();
        this.noticeService = noticeService != null ? noticeService : new NoticeService();
        this.userService = userService != null ? userService : new UserService();
    }

    // =========================================================================
    // 1. Course Operations
    // =========================================================================

    public List<Course> getAllCourses() throws DatabaseException {
        return courseService.getAllCourses();
    }

    public Course getCourseByCode(String courseCode) throws DatabaseException {
        return courseService.getCourseByCode(courseCode);
    }

    // =========================================================================
    // 2. Attendance & Daily Session Operations
    // =========================================================================

    public List<Undergraduate> getAllUndergraduates() {
        return attendanceService.getAllUndergraduates();
    }

    public int getOrCreateSession(String courseCode, String sessionType, int sessionNumber, LocalDate date, int createdBy)
            throws ValidationException, DatabaseException {
        return attendanceService.getOrCreateSession(courseCode, sessionType, sessionNumber, date, createdBy);
    }

    public Map<Integer, String> getAttendanceForSession(int sessionId) throws DatabaseException {
        return attendanceService.getAttendanceForSession(sessionId);
    }

    public boolean saveBatchAttendance(int sessionId, Map<Integer, String> studentAttendance)
            throws ValidationException, DatabaseException {
        return attendanceService.saveBatchAttendance(sessionId, studentAttendance);
    }

    public boolean deleteSessionAttendance(int sessionId) throws DatabaseException {
        return attendanceService.deleteSessionAttendance(sessionId);
    }

    public boolean hasAttendanceRecords(int sessionId) {
        return attendanceService.hasAttendanceRecords(sessionId);
    }

    // =========================================================================
    // 3. Batch Attendance Summary & 80% Eligibility Operations
    // =========================================================================

    public List<StudentAttendanceSummary> getBatchAttendanceSummary(String courseCode, String sessionTypeFilter) {
        return eligibilityService.getBatchAttendanceSummary(courseCode, sessionTypeFilter);
    }

    public List<StudentAttendanceSummary> filterSummariesByEligibility(List<StudentAttendanceSummary> summaries, String filter) {
        return eligibilityService.filterSummariesByEligibility(summaries, filter);
    }

    public double calculateBatchAverage(List<StudentAttendanceSummary> summaries) {
        return eligibilityService.calculateBatchAverage(summaries);
    }

    public int countEligible(List<StudentAttendanceSummary> summaries) {
        return eligibilityService.countEligible(summaries);
    }

    public int countIneligible(List<StudentAttendanceSummary> summaries) {
        return eligibilityService.countIneligible(summaries);
    }

    // =========================================================================
    // 4. Medical Certificate Operations
    // =========================================================================

    public List<Medical> getAllMedicals(String statusFilter) {
        return medicalService.getAllMedicals(statusFilter);
    }

    public boolean updateMedicalStatus(int medicalId, String status) throws ValidationException, DatabaseException {
        return medicalService.updateMedicalStatus(medicalId, status);
    }

    public boolean addMedical(Medical medical) throws ValidationException, DatabaseException {
        return medicalService.addMedical(medical);
    }

    public List<Medical> getMedicalsByStudent(int studentId) throws DatabaseException {
        return medicalService.getMedicalsByStudent(studentId);
    }

    // =========================================================================
    // 5. Department Timetable & Schedules
    // =========================================================================

    public List<TimetableEntry> getDepartmentTimetable(int semesterNumber, String academicYear) throws DatabaseException {
        return timetableService.getFullTimetable(semesterNumber, academicYear);
    }

    // =========================================================================
    // 6. Faculty Notice Board Operations
    // =========================================================================

    public List<Notice> getAllNotices() throws DatabaseException {
        return noticeService.getAllNotices();
    }

    public List<Notice> getNoticesByCategory(String category) throws DatabaseException {
        return noticeService.getNoticesByCategory(category);
    }

    // =========================================================================
    // 7. Profile & Account Management
    // =========================================================================

    public boolean updateProfile(User user) throws ValidationException, DatabaseException {
        return userService.updateUser(user);
    }

    public User getUserById(int userId) throws DatabaseException {
        return userService.getUserById(userId);
    }
}
