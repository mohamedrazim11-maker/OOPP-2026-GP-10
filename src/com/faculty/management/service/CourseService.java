package com.faculty.management.service;

<<<<<<< HEAD
=======
import com.faculty.management.config.DatabaseConnection;
>>>>>>> Aysha
import com.faculty.management.dao.CourseDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.Course;
import com.faculty.management.util.ValidationUtil;

<<<<<<< HEAD
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
=======
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service layer handling Course management business rules, validations, and database persistence.
 * Demonstrates Classes & Objects, Encapsulation, Abstraction, Polymorphism,
 * Error/Exception Handling, and Database Handling.
>>>>>>> Aysha
 */
public class CourseService {

    private final CourseDAO courseDAO;

<<<<<<< HEAD
=======
    // In-memory mock storage for fallback demo mode when database is offline
    private static final List<Course> mockCourses = new ArrayList<>();
    private static final AtomicInteger mockIdGenerator = new AtomicInteger(10);

    static {
        initMockData();
    }

>>>>>>> Aysha
    public CourseService() {
        this.courseDAO = new CourseDAO();
    }

    public CourseService(CourseDAO courseDAO) {
<<<<<<< HEAD
        this.courseDAO = (courseDAO != null) ? courseDAO : new CourseDAO();
    }

    /**
     * Adds a new course to the database after validation and uniqueness checks.
     *
     * @param course Course entity to add
     * @return Saved Course entity with assigned database ID
=======
        this.courseDAO = courseDAO;
    }

    /**
     * Adds a new course to the system after validation and uniqueness checks.
     *
     * @param course Course entity to add
     * @return Saved Course entity with assigned ID
>>>>>>> Aysha
     * @throws ValidationException If validation fails (empty fields, duplicate code)
     * @throws DatabaseException   If database error occurs
     */
    public Course addCourse(Course course) throws ValidationException, DatabaseException {
        // 1. Validation (Encapsulation & Exception Handling)
        ValidationUtil.validateCourse(course);

        String trimmedCode = course.getCourseCode().trim().toUpperCase();
        course.setCourseCode(trimmedCode);

<<<<<<< HEAD
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
=======
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
>>>>>>> Aysha
    }

    /**
     * Polymorphic overload to add a course using individual parameter values.
<<<<<<< HEAD
     * Demonstrates Polymorphism and Abstraction.
=======
>>>>>>> Aysha
     */
    public Course addCourse(String courseCode, String courseName, int creditValue, int theoryHours,
                            int practicalHours, String department, String semester, String description)
            throws ValidationException, DatabaseException {
        Course course = new Course(courseCode, courseName, creditValue, theoryHours, practicalHours, department, semester, description);
        return addCourse(course);
    }

    /**
<<<<<<< HEAD
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
=======
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
>>>>>>> Aysha
     */
    public Course getCourseByCode(String courseCode) throws DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return null;
        }
        String trimmed = courseCode.trim().toUpperCase();
<<<<<<< HEAD
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
=======
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
>>>>>>> Aysha
     */
    public boolean updateCourse(Course course) throws ValidationException, DatabaseException {
        if (course == null || course.getCourseId() <= 0) {
            throw new ValidationException("Invalid Course specified for update.");
        }

        ValidationUtil.validateCourse(course);
        String trimmedCode = course.getCourseCode().trim().toUpperCase();
        course.setCourseCode(trimmedCode);

<<<<<<< HEAD
        // Check uniqueness excluding current course ID
        if (courseDAO.existsByCodeExcludingCourse(trimmedCode, course.getCourseId())) {
            throw new ValidationException("Course Code '" + trimmedCode + "' is already in use by another course in the database.");
        }

        // Direct SQL UPDATE via CourseDAO
        return courseDAO.updateCourse(course);
=======
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
>>>>>>> Aysha
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
<<<<<<< HEAD
     * Polymorphic overload to update a course using course code identifier.
     * Demonstrates Polymorphism and Abstraction.
     */
    public boolean updateCourse(String courseCode, String courseName, int creditValue, int theoryHours,
                                int practicalHours, String department, String semester, String description)
            throws ValidationException, DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new ValidationException("Course Code cannot be empty for update.");
        }
        Course existing = getCourseByCode(courseCode.trim());
        if (existing == null) {
            throw new ValidationException("Course with code '" + courseCode + "' does not exist in the database.");
        }
        return updateCourse(existing.getCourseId(), courseCode, courseName, creditValue, theoryHours, practicalHours, department, semester, description);
    }

    /**
     * Searches courses by course code or course name substring directly from MySQL database.
     * Demonstrates Abstraction and Database Handling for View Courses option.
     *
     * @param keyword Search term
     * @return List of matching courses
     * @throws DatabaseException If query fails
     */
    public List<Course> searchCourses(String keyword) throws DatabaseException {
        return courseDAO.searchCourses(keyword);
    }

    /**
     * Retrieves courses filtered by academic department for View Courses option.
     * Demonstrates Abstraction and Database Handling.
     */
    public List<Course> getCoursesByDepartment(String department) throws DatabaseException {
        return courseDAO.getCoursesByDepartment(department);
    }

    /**
     * Retrieves the total count of registered courses in the database.
     * Demonstrates Database Handling.
     */
    public int getCourseCount() throws DatabaseException {
        return courseDAO.getCourseCount();
    }

    /**
     * Deletes a course by its ID directly from MySQL database.
=======
     * Deletes a course by its ID.
>>>>>>> Aysha
     *
     * @param courseId ID of the course to delete
     * @return true if deleted
     * @throws ValidationException If validation fails
<<<<<<< HEAD
     * @throws DatabaseException   If database deletion fails
=======
     * @throws DatabaseException   If database error occurs
>>>>>>> Aysha
     */
    public boolean deleteCourse(int courseId) throws ValidationException, DatabaseException {
        if (courseId <= 0) {
            throw new ValidationException("Invalid Course ID specified for deletion.");
        }

        Course course = getCourseById(courseId);
        if (course == null) {
<<<<<<< HEAD
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
=======
            throw new ValidationException("Course with ID " + courseId + " does not exist.");
        }

        if (DatabaseConnection.getInstance().isConnected()) {
            boolean deleted = courseDAO.deleteCourse(courseId);
            mockCourses.removeIf(c -> c.getCourseId() == courseId);
            return deleted;
        }

        return mockCourses.removeIf(c -> c.getCourseId() == courseId);
>>>>>>> Aysha
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

<<<<<<< HEAD
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
=======
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
>>>>>>> Aysha
    }
}
