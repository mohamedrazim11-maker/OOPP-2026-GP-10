package com.faculty.management.service;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Course;
import com.faculty.management.util.ValidationUtil;

import java.util.List;

/**
 * Service layer handling Course management business rules, validations, and real MySQL database persistence.
 * 
 * Directly connected to MySQL via CourseDAO using pure JDBC.
 * Completely free of mock or hardcoded data.
 * 
 * OOP Principles Applied:
 * - Classes & Objects: Instantiates Course domain models and DAO components.
 * - Inheritance: Object inheritance for entity models and exceptions.
 * - Abstraction: Encapsulates SQL queries behind service methods.
 * - Polymorphism: Overloaded methods for entity-based, code-based, and parameter-based operations.
 * - Encapsulation: Private CourseDAO and validation logic protecting domain integrity.
 * - Error/Exception Handling: Uses ValidationException and DatabaseException.
 * - Database Handling: Parameterized SQL CRUD operations via CourseDAO.
 */
public class CourseService {

    private final CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
    }

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = (courseDAO != null) ? courseDAO : new CourseDAO();
    }

    /**
     * Adds a new course to the database after validation and uniqueness checks.
     *
     * @param course Course entity to add
     * @return Saved Course entity with assigned database ID
     * @throws ValidationException If validation fails (empty fields, duplicate code)
     * @throws DatabaseException   If database error occurs
     */
    public Course addCourse(Course course) throws ValidationException, DatabaseException {
        // 1. Validation (Encapsulation & Exception Handling)
        ValidationUtil.validateCourse(course);

        String trimmedCode = course.getCourseCode().trim().toUpperCase();
        course.setCourseCode(trimmedCode);

        // Derive academic curriculum metadata if absent (req.txt)
        if (course.getSemesterNumber() <= 0) {
            int semNum = (course.getSemester() != null && course.getSemester().contains("1")) ? 1 : 2;
            course.setSemesterNumber(semNum);
        }
        if (course.getAcademicYear() == null || course.getAcademicYear().trim().isEmpty()) {
            course.setAcademicYear("2025/2026");
        }

        // 2. Uniqueness check against MySQL database
        if (courseDAO.existsByCode(trimmedCode)) {
            throw new ValidationException("Course Code '" + trimmedCode + "' already exists in the database. Please use a unique course code.");
        }

        // 3. Direct SQL INSERT via CourseDAO
        return courseDAO.createCourse(course);
    }

    /**
     * Polymorphic overload to add a course using individual parameter values.
     * Demonstrates Polymorphism and Abstraction.
     */
    public Course addCourse(String courseCode, String courseName, int creditValue, int theoryHours,
                            int practicalHours, String department, String semester, String description)
            throws ValidationException, DatabaseException {
        Course course = new Course(courseCode, courseName, creditValue, theoryHours, practicalHours, department, semester, description);
        return addCourse(course);
    }

    /**
     * Polymorphic overload to add a standard course with default 30h theory and 30h practical contact hours.
     * Demonstrates Polymorphism and Abstraction.
     */
    public Course addCourse(String courseCode, String courseName, int creditValue,
                            String department, String semester)
            throws ValidationException, DatabaseException {
        int defaultTheory = (creditValue >= 2) ? 30 : 15;
        int defaultPractical = (creditValue >= 3) ? 30 : 0;
        return addCourse(courseCode, courseName, creditValue, defaultTheory, defaultPractical, department, semester, "");
    }

    /**
     * Polymorphic overload to add a full curriculum course with explicit semester number and academic year.
     * Demonstrates Polymorphism and Abstraction.
     */
    public Course addCourse(String courseCode, String courseName, int creditValue, int theoryHours,
                            int practicalHours, String department, String semester, int semesterNumber,
                            String academicYear, String description)
            throws ValidationException, DatabaseException {
        Course course = new Course(courseCode, courseName, creditValue, theoryHours, practicalHours, department, semester, description);
        course.setSemesterNumber(semesterNumber);
        course.setAcademicYear(academicYear);
        return addCourse(course);
    }

    /**
     * Retrieves all courses in the system directly from MySQL database.
     *
     * @return List of Course entities from courses table
     * @throws DatabaseException If database query fails
     */
    public List<Course> getAllCourses() throws DatabaseException {
        return courseDAO.getAllCourses();
    }

    /**
     * Retrieves a course by its unique primary key ID from MySQL.
     *
     * @param courseId ID of the course
     * @return Course entity or null if not found
     * @throws DatabaseException If database query fails
     */
    public Course getCourseById(int courseId) throws DatabaseException {
        if (courseId <= 0) {
            return null;
        }
        return courseDAO.findById(courseId);
    }

    /**
     * Retrieves a course by its unique course code from MySQL.
     *
     * @param courseCode Course code (e.g. "ICT2132")
     * @return Course entity or null
     * @throws DatabaseException If database query fails
     */
    public Course getCourseByCode(String courseCode) throws DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return null;
        }
        String trimmed = courseCode.trim().toUpperCase();
        return courseDAO.findByCode(trimmed);
    }

    /**
     * Polymorphic overload to retrieve course entity details from Course object.
     *
     * @param course Course entity
     * @return Full Course entity
     * @throws ValidationException If course is null or invalid
     * @throws DatabaseException   If database error occurs
     */
    public Course getCourse(Course course) throws ValidationException, DatabaseException {
        if (course == null) {
            throw new ValidationException("Course entity cannot be null.");
        }
        return getCourseById(course.getCourseId());
    }

    /**
     * Updates an existing course record in MySQL with validation and uniqueness checks.
     *
     * @param course Course entity with updated values
     * @return true if updated successfully in MySQL
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database update fails
     */
    public boolean updateCourse(Course course) throws ValidationException, DatabaseException {
        if (course == null || course.getCourseId() <= 0) {
            throw new ValidationException("Invalid Course specified for update.");
        }

        ValidationUtil.validateCourse(course);
        String trimmedCode = course.getCourseCode().trim().toUpperCase();
        course.setCourseCode(trimmedCode);

        // Check uniqueness excluding current course ID
        if (courseDAO.existsByCodeExcludingCourse(trimmedCode, course.getCourseId())) {
            throw new ValidationException("Course Code '" + trimmedCode + "' is already in use by another course in the database.");
        }

        // Direct SQL UPDATE via CourseDAO
        return courseDAO.updateCourse(course);
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
     * Deletes a course by its ID directly from MySQL database.
     *
     * @param courseId ID of the course to delete
     * @return true if deleted
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database deletion fails
     */
    public boolean deleteCourse(int courseId) throws ValidationException, DatabaseException {
        if (courseId <= 0) {
            throw new ValidationException("Invalid Course ID specified for deletion.");
        }

        Course course = getCourseById(courseId);
        if (course == null) {
            throw new ValidationException("Course with ID " + courseId + " does not exist in the database.");
        }

        return courseDAO.deleteCourse(courseId);
    }

    /**
     * Polymorphic overload to delete a course using a Course Code string.
     *
     * @param courseCode Course code (e.g. "ICT2132")
     * @return true if deleted
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean deleteCourse(String courseCode) throws ValidationException, DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new ValidationException("Course Code cannot be empty for deletion.");
        }
        Course course = getCourseByCode(courseCode.trim());
        if (course == null) {
            throw new ValidationException("Course with code '" + courseCode + "' does not exist in the database.");
        }
        return deleteCourse(course.getCourseId());
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

    /**
     * Assigns course information (department, semester, credits, contact hours, syllabus) to a course.
     * Validates input rules and persists updates directly to MySQL database via CourseDAO.
     * Demonstrates Abstraction, Encapsulation, Error/Exception Handling, and Database Handling.
     *
     * @param course Course entity containing updated assignment information
     * @return true if assigned and saved successfully
     * @throws ValidationException If validation constraints fail
     * @throws DatabaseException   If database error occurs
     */
    public boolean assignCourseInformation(Course course) throws ValidationException, DatabaseException {
        if (course == null || course.getCourseId() <= 0) {
            throw new ValidationException("Please select a valid course to assign information.");
        }

        ValidationUtil.validateCourseAssignment(course);

        Course existing = getCourseById(course.getCourseId());
        if (existing == null) {
            throw new ValidationException("Course with ID " + course.getCourseId() + " does not exist in the database.");
        }

        return courseDAO.assignCourseInformation(course);
    }

    /**
     * Polymorphic overload to assign course information using individual parameter values.
     *
     * @param courseId       Course ID
     * @param department     Department to assign
     * @param semester       Semester to assign
     * @param creditValue    Credits to assign
     * @param theoryHours    Theory hours to assign
     * @param practicalHours Practical hours to assign
     * @param description    Syllabus description
     * @return true if assigned
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean assignCourseInformation(int courseId, String department, String semester, int creditValue,
                                           int theoryHours, int practicalHours, String description)
            throws ValidationException, DatabaseException {
        ValidationUtil.validateCourseAssignment(courseId, department, semester, creditValue, theoryHours, practicalHours);

        Course course = getCourseById(courseId);
        if (course == null) {
            throw new ValidationException("Course with ID " + courseId + " does not exist in the database.");
        }
        course.setDepartment(department);
        course.setSemester(semester);
        course.setCreditValue(creditValue);
        course.setTheoryHours(theoryHours);
        course.setPracticalHours(practicalHours);
        course.setDescription(description);

        return assignCourseInformation(course);
    }

    /**
     * Polymorphic overload to assign course information using unique course code.
     *
     * @param courseCode     Course code (e.g. ICT2132)
     * @param department     Department to assign
     * @param semester       Semester to assign
     * @param creditValue    Credits to assign
     * @param theoryHours    Theory hours to assign
     * @param practicalHours Practical hours to assign
     * @param description    Syllabus description
     * @return true if assigned
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean assignCourseInformation(String courseCode, String department, String semester, int creditValue,
                                           int theoryHours, int practicalHours, String description)
            throws ValidationException, DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new ValidationException("Course code cannot be empty for assigning information.");
        }
        Course course = getCourseByCode(courseCode.trim());
        if (course == null) {
            throw new ValidationException("Course with code '" + courseCode + "' does not exist in the database.");
        }
        return assignCourseInformation(course.getCourseId(), department, semester, creditValue, theoryHours, practicalHours, description);
    }
}
