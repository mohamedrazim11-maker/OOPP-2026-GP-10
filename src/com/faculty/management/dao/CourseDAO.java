package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course entity operations using JDBC.
 * Demonstrates Database Handling, Abstraction, Polymorphism, and Exception Handling.
 */
public class CourseDAO {

    /**
     * Inserts a new course record into the courses table using parameterized JDBC.
     * Demonstrates Database Handling, Abstraction, and Exception Handling.
     *
     * @param course Course entity to insert
     * @return Saved Course entity with generated courseId
     * @throws DatabaseException If database operation fails
     */
    public Course createCourse(Course course) throws DatabaseException {
        if (course == null) {
            throw new DatabaseException("Course entity cannot be null for database insertion.");
        }

        Connection conn = DatabaseConnection.getInstance().getConnection();

        // Calculate helper values based on academic requirements (req.txt)
        int creditVal = course.getCreditValue() > 0 ? course.getCreditValue() : 2;
        int theoryHours = course.getTheoryHours();
        int practicalHours = course.getPracticalHours();
        int theorySessions = (theoryHours > 0) ? Math.max(1, (theoryHours + 1) / 2) : 15;
        int practicalSessions = (practicalHours > 0) ? Math.max(1, (practicalHours + 1) / 2) : 0;
        int semesterNum = course.getSemesterNumber() > 0 ? course.getSemesterNumber() :
                (course.getSemester() != null && course.getSemester().contains("1") ? 1 : 2);
        String academicYear = course.getAcademicYear() != null && !course.getAcademicYear().isEmpty() ?
                course.getAcademicYear() : "2025/2026";

        boolean hasExtendedCols = hasColumn(conn, "courses", "credits");

        String sql;
        if (hasExtendedCols) {
            sql = "INSERT INTO courses (course_code, course_name, credit_value, credits, theory_hours, practical_hours, " +
                  "theory_sessions, practical_sessions, department, semester, semester_number, academic_year, description) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO courses (course_code, course_name, credit_value, theory_hours, practical_hours, " +
                  "department, semester, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, creditVal);

            if (hasExtendedCols) {
                stmt.setInt(4, creditVal); // credits
                stmt.setInt(5, theoryHours);
                stmt.setInt(6, practicalHours);
                stmt.setInt(7, theorySessions);
                stmt.setInt(8, practicalSessions);
                stmt.setString(9, course.getDepartment());
                stmt.setString(10, course.getSemester());
                stmt.setInt(11, semesterNum);
                stmt.setString(12, academicYear);
                stmt.setString(13, course.getDescription());
            } else {
                stmt.setInt(4, theoryHours);
                stmt.setInt(5, practicalHours);
                stmt.setString(6, course.getDepartment());
                stmt.setString(7, course.getSemester());
                stmt.setString(8, course.getDescription());
            }

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating course failed, no rows affected in database.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setCourseId(generatedKeys.getInt(1));
                }
            }

            return course;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert course into database: " + e.getMessage(), e);
        }
    }

    /**
     * Polymorphic overload to insert a new course into the database using individual parameter fields.
     * Demonstrates Polymorphism and Database Handling.
     *
     * @param courseCode     Course code (e.g. ICT2132)
     * @param courseName     Course name
     * @param creditValue    Credits (1-10)
     * @param theoryHours    Theory hours
     * @param practicalHours Practical hours
     * @param department     Department name
     * @param semester       Semester name
     * @param description    Course description
     * @return Saved Course entity
     * @throws DatabaseException If database operation fails
     */
    public Course createCourse(String courseCode, String courseName, int creditValue, int theoryHours,
                               int practicalHours, String department, String semester, String description) throws DatabaseException {
        Course course = new Course(courseCode, courseName, creditValue, theoryHours, practicalHours, department, semester, description);
        return createCourse(course);
    }

    /**
     * Checks if a column exists in the specified database table.
     */
    private boolean hasColumn(Connection conn, String tableName, String columnName) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
                if (rs.next()) {
                    return true;
                }
            }
            try (ResultSet rs = meta.getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase())) {
                return rs.next();
            }
        } catch (SQLException ignored) {
            // Default to false if metadata lookup fails
        }
        return false;
    }

    /**
     * Retrieves all courses ordered by course code.
     *
     * @return List of Course entities
     * @throws DatabaseException If database operation fails
     */
    public List<Course> getAllCourses() throws DatabaseException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, course_code, course_name, credit_value, theory_hours, practical_hours, " +
                     "department, semester, description FROM courses ORDER BY course_code ASC";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch courses from database: " + e.getMessage(), e);
        }

        return courses;
    }

    /**
     * Alias for getAllCourses() for cross-module compatibility.
     */
    public List<Course> findAll() throws DatabaseException {
        return getAllCourses();
    }

    /**
     * Finds a course by unique course ID.
     *
     * @param courseId ID of the course
     * @return Course entity or null if not found
     * @throws DatabaseException If database operation fails
     */
    public Course findById(int courseId) throws DatabaseException {
        String sql = "SELECT course_id, course_code, course_name, credit_value, theory_hours, practical_hours, " +
                     "department, semester, description FROM courses WHERE course_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find course by ID: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Finds a course by unique course code.
     *
     * @param courseCode Course code (e.g. "ICT2132")
     * @return Course entity or null if not found
     * @throws DatabaseException If database operation fails
     */
    public Course findByCode(String courseCode) throws DatabaseException {
        String sql = "SELECT course_id, course_code, course_name, credit_value, theory_hours, practical_hours, " +
                     "department, semester, description FROM courses WHERE course_code = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find course by code: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Checks if a course exists with the given course code.
     *
     * @param courseCode Code to check
     * @return true if exists, false otherwise
     * @throws DatabaseException If database operation fails
     */
    public boolean existsByCode(String courseCode) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM courses WHERE course_code = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check course code existence: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Checks if a course code already exists for another course (excluding a given course ID).
     */
    public boolean existsByCodeExcludingCourse(String courseCode, int courseId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM courses WHERE course_code = ? AND course_id != ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check course code uniqueness: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Updates an existing course record in the database.
     *
     * @param course Course entity with updated values
     * @return true if updated successfully
     * @throws DatabaseException If database operation fails
     */
    public boolean updateCourse(Course course) throws DatabaseException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credit_value = ?, theory_hours = ?, " +
                     "practical_hours = ?, department = ?, semester = ?, description = ? WHERE course_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCreditValue());
            stmt.setInt(4, course.getTheoryHours());
            stmt.setInt(5, course.getPracticalHours());
            stmt.setString(6, course.getDepartment());
            stmt.setString(7, course.getSemester());
            stmt.setString(8, course.getDescription());
            stmt.setInt(9, course.getCourseId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("No course found with ID " + course.getCourseId() + " to update.");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update course in database: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a course by unique course ID.
     *
     * @param courseId ID of the course to delete
     * @return true if deleted
     * @throws DatabaseException If database operation fails
     */
    public boolean deleteCourse(int courseId) throws DatabaseException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("No course found with ID " + courseId + " to delete.");
            }
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451 || (e.getMessage() != null && e.getMessage().contains("foreign key constraint"))) {
                throw new DatabaseException("Cannot delete course because student marks, attendance, or materials exist for it.", e);
            }
            throw new DatabaseException("Failed to delete course: " + e.getMessage(), e);
        }
    }

    /**
     * Polymorphic overload to delete a course using its unique Course Code.
     *
     * @param courseCode Course code (e.g. "ICT2132")
     * @return true if deleted
     * @throws DatabaseException If database operation fails
     */
    public boolean deleteCourse(String courseCode) throws DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new DatabaseException("Course code cannot be empty for deletion.");
        }
        Course course = findByCode(courseCode.trim());
        if (course == null) {
            throw new DatabaseException("No course found with code '" + courseCode + "' to delete.");
        }
        return deleteCourse(course.getCourseId());
    }

    /**
     * Polymorphic overload to delete a course using a Course entity.
     *
     * @param course Course entity to delete
     * @return true if deleted
     * @throws DatabaseException If database operation fails
     */
    public boolean deleteCourse(Course course) throws DatabaseException {
        if (course == null) {
            throw new DatabaseException("Course entity cannot be null.");
        }
        return deleteCourse(course.getCourseId());
    }

    /**
     * Assigns course information (department, semester, credit value, theory & practical contact hours,
     * description) to an existing course in the database.
     * Demonstrates Database Handling, Abstraction, and Exception Handling.
     *
     * @param course Course entity containing assigned information
     * @return true if updated successfully in MySQL
     * @throws DatabaseException If database operation fails
     */
    public boolean assignCourseInformation(Course course) throws DatabaseException {
        if (course == null || course.getCourseId() <= 0) {
            throw new DatabaseException("Invalid course specified for assigning information.");
        }

        String sql = "UPDATE courses SET department = ?, semester = ?, credit_value = ?, theory_hours = ?, " +
                     "practical_hours = ?, description = ? WHERE course_id = ?";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getDepartment());
            stmt.setString(2, course.getSemester());
            stmt.setInt(3, course.getCreditValue());
            stmt.setInt(4, course.getTheoryHours());
            stmt.setInt(5, course.getPracticalHours());
            stmt.setString(6, course.getDescription());
            stmt.setInt(7, course.getCourseId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("No course found with ID " + course.getCourseId() + " to assign information.");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to assign course information in database: " + e.getMessage(), e);
        }
    }

    /**
     * Polymorphic overload to assign course information using course ID and specific parameters.
     *
     * @param courseId       Course ID
     * @param department     Assigned Department
     * @param semester       Assigned Semester
     * @param creditValue    Assigned Credits
     * @param theoryHours    Assigned Theory Hours
     * @param practicalHours Assigned Practical Hours
     * @param description    Assigned Description
     * @return true if assigned
     * @throws DatabaseException If database operation fails
     */
    public boolean assignCourseInformation(int courseId, String department, String semester, int creditValue,
                                           int theoryHours, int practicalHours, String description) throws DatabaseException {
        Course course = findById(courseId);
        if (course == null) {
            throw new DatabaseException("Course with ID " + courseId + " was not found to assign information.");
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
     * @param department     Assigned Department
     * @param semester       Assigned Semester
     * @param creditValue    Assigned Credits
     * @param theoryHours    Assigned Theory Hours
     * @param practicalHours Assigned Practical Hours
     * @param description    Assigned Description
     * @return true if assigned
     * @throws DatabaseException If database operation fails
     */
    public boolean assignCourseInformation(String courseCode, String department, String semester, int creditValue,
                                           int theoryHours, int practicalHours, String description) throws DatabaseException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new DatabaseException("Course code cannot be empty for assigning information.");
        }
        Course course = findByCode(courseCode.trim());
        if (course == null) {
            throw new DatabaseException("Course with code '" + courseCode + "' was not found to assign information.");
        }
        return assignCourseInformation(course.getCourseId(), department, semester, creditValue, theoryHours, practicalHours, description);
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setCreditValue(rs.getInt("credit_value"));
        c.setTheoryHours(rs.getInt("theory_hours"));
        c.setPracticalHours(rs.getInt("practical_hours"));
        c.setDepartment(rs.getString("department"));
        c.setSemester(rs.getString("semester"));
        c.setDescription(rs.getString("description"));
        return c;
    }

    Course mapRow(ResultSet rs) throws SQLException {
        return mapResultSetToCourse(rs);
    }
}
