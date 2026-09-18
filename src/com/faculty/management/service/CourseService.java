package com.faculty.management.service;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.dao.CourseDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Course;
import com.faculty.management.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service layer handling Course management business rules, validations, and database persistence.
 * Demonstrates Classes & Objects, Encapsulation, Abstraction, Polymorphism,
 * Error/Exception Handling, and Database Handling.
 */
public class CourseService {

    private final CourseDAO courseDAO;

    // In-memory mock storage for fallback demo mode when database is offline
    private static final List<Course> mockCourses = new ArrayList<>();
    private static final AtomicInteger mockIdGenerator = new AtomicInteger(10);

    static {
        initMockData();
    }

    public CourseService() {
        this.courseDAO = new CourseDAO();
    }

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    /**
     * Adds a new course to the system after validation and uniqueness checks.
     *
     * @param course Course entity to add
     * @return Saved Course entity with assigned ID
     * @throws ValidationException If validation fails (empty fields, duplicate code)
     * @throws DatabaseException   If database error occurs
     */
    public Course addCourse(Course course) throws ValidationException, DatabaseException {
        // 1. Validation (Encapsulation & Exception Handling)
        ValidationUtil.validateCourse(course);

        String trimmedCode = course.getCourseCode().trim().toUpperCase();
        course.setCourseCode(trimmedCode);

        // 2. Database persistence when connected
        if (DatabaseConnection.getInstance().isConnected()) {
            if (courseDAO.existsByCode(trimmedCode)) {
                throw new ValidationException("Course Code '" + trimmedCode + "' already exists in the system.");
            }
            Course saved = courseDAO.createCourse(course);
            mockCourses.add(saved);
            return saved;
        }

        // 3. Offline Fallback Demo Mode
        for (Course c : mockCourses) {
            if (c.getCourseCode().equalsIgnoreCase(trimmedCode)) {
                throw new ValidationException("Course Code '" + trimmedCode + "' already exists in the system.");
            }
        }

        course.setCourseId(mockIdGenerator.incrementAndGet());
        mockCourses.add(course);
        return course;
    }

    /**
     * Polymorphic overload to add a course using individual parameter values.
     */
    public Course addCourse(String courseCode, String courseName, int creditValue, int theoryHours,
                            int practicalHours, String department, String semester, String description)
            throws ValidationException, DatabaseException {
        Course course = new Course(courseCode, courseName, creditValue, theoryHours, practicalHours, department, semester, description);
        return addCourse(course);
    }

    /**
     * Retrieves all courses in the system.
     *
     * @return List of Course entities
     * @throws DatabaseException If database error occurs
     */
    public List<Course> getAllCourses() throws DatabaseException {
        if (DatabaseConnection.getInstance().isConnected()) {
            return courseDAO.getAllCourses();
        }
        return new ArrayList<>(mockCourses);
    }

    /**
     * Retrieves a course by its unique ID.
     *
     * @param courseId ID of the course
     * @return Course entity or null if not found
     * @throws DatabaseException If database error occurs
     */
    public Course getCourseById(int courseId) throws DatabaseException {
        if (DatabaseConnection.getInstance().isConnected()) {
            return courseDAO.findById(courseId);
        }
        for (Course c : mockCourses) {
            if (c.getCourseId() == courseId) {
                return c;
            }
        }
        return null;
    }

    /**
     * Retrieves a course by its course code.
     *
     * @param courseCode Course code (e.g. "ICT2132")
     * @return Course entity or null
     * @throws DatabaseException If database error occurs
     */
    public Course getCourseByCode(String courseCode) throws DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return null;
        }
        String trimmed = courseCode.trim().toUpperCase();
        if (DatabaseConnection.getInstance().isConnected()) {
            return courseDAO.findByCode(trimmed);
        }
        for (Course c : mockCourses) {
            if (c.getCourseCode().equalsIgnoreCase(trimmed)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Updates an existing course record with validation and uniqueness checks.
     *
     * @param course Course entity with updated values
     * @return true if updated successfully
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean updateCourse(Course course) throws ValidationException, DatabaseException {
        if (course == null || course.getCourseId() <= 0) {
            throw new ValidationException("Invalid Course specified for update.");
        }

        ValidationUtil.validateCourse(course);
        String trimmedCode = course.getCourseCode().trim().toUpperCase();
        course.setCourseCode(trimmedCode);

        // Database persistence when connected
        if (DatabaseConnection.getInstance().isConnected()) {
            if (courseDAO.existsByCodeExcludingCourse(trimmedCode, course.getCourseId())) {
                throw new ValidationException("Course Code '" + trimmedCode + "' is already in use by another course.");
            }
            boolean updated = courseDAO.updateCourse(course);
            updateMockCourse(course);
            return updated;
        }

        // Offline Fallback Demo Mode
        for (Course c : mockCourses) {
            if (c.getCourseId() != course.getCourseId() && c.getCourseCode().equalsIgnoreCase(trimmedCode)) {
                throw new ValidationException("Course Code '" + trimmedCode + "' is already in use by another course.");
            }
        }

        return updateMockCourse(course);
    }

    /**
     * Polymorphic overload to update a course using individual parameter values.
     */
    public boolean updateCourse(int courseId, String courseCode, String courseName, int creditValue,
                                int theoryHours, int practicalHours, String department, String semester, String description)
            throws ValidationException, DatabaseException {
        Course course = new Course(courseId, courseCode, courseName, creditValue, theoryHours, practicalHours, department, semester, description);
        return updateCourse(course);
    }

    /**
     * Deletes a course by its ID.
     *
     * @param courseId ID of the course to delete
     * @return true if deleted
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean deleteCourse(int courseId) throws ValidationException, DatabaseException {
        if (courseId <= 0) {
            throw new ValidationException("Invalid Course ID specified for deletion.");
        }

        Course course = getCourseById(courseId);
        if (course == null) {
            throw new ValidationException("Course with ID " + courseId + " does not exist.");
        }

        if (DatabaseConnection.getInstance().isConnected()) {
            boolean deleted = courseDAO.deleteCourse(courseId);
            mockCourses.removeIf(c -> c.getCourseId() == courseId);
            return deleted;
        }

        return mockCourses.removeIf(c -> c.getCourseId() == courseId);
    }

    /**
     * Polymorphic overload to delete a course using a Course entity.
     *
     * @param course Course entity to delete
     * @return true if deleted
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean deleteCourse(Course course) throws ValidationException, DatabaseException {
        if (course == null) {
            throw new ValidationException("Course entity cannot be null.");
        }
        return deleteCourse(course.getCourseId());
    }

    private boolean updateMockCourse(Course course) {
        for (int i = 0; i < mockCourses.size(); i++) {
            if (mockCourses.get(i).getCourseId() == course.getCourseId()) {
                mockCourses.set(i, course);
                return true;
            }
        }
        return false;
    }

    private static void initMockData() {
        mockCourses.add(new Course(1, "ICT2112", "Data Structures and Algorithms", 2, 30, 30, "Department of Information & Communication Technology", "Semester 2", "Core data structures and algorithmic complexity"));
        mockCourses.add(new Course(2, "ICT2123", "Database Management Systems", 3, 30, 60, "Department of Information & Communication Technology", "Semester 2", "Relational database concepts, normalization, and SQL"));
        mockCourses.add(new Course(3, "ICT2132", "Object Oriented Programming Practicum", 2, 0, 60, "Department of Information & Communication Technology", "Semester 2", "Hands-on object-oriented programming with Java Swing & MySQL"));
        mockCourses.add(new Course(4, "BST2113", "Bioprocess Technology", 3, 30, 60, "Department of Biosystems Technology", "Semester 2", "Bioprocess fundamentals and industrial biotechnology applications"));
        mockCourses.add(new Course(5, "ENG2113", "Engineering Mechanics", 3, 30, 60, "Department of Engineering Technology", "Semester 2", "Applied statics, dynamics, and engineering mechanics principles"));
    }
}
