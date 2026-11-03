package com.faculty.management.dao;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.InvalidMarkException;
import com.faculty.management.model.Evaluation;
import com.faculty.management.model.Mark;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Marks & Evaluations.
 * Handles Database CRUD operations, supporting both evaluation-specific marks and component course marks.
 * Demonstrates Database Handling, Exception Handling, and Abstraction.
 */
public class MarkDAO {

    /**
     * Checks if a column exists in a given table.
     */
    private boolean hasColumn(Connection conn, String tableName, String columnName) {
        try {
            DatabaseMetaData md = conn.getMetaData();
            try (ResultSet rs = md.getColumns(null, null, tableName, columnName)) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Inserts a new student mark entry into the database.
     */
    public boolean insertMark(Mark mark) throws DatabaseException, SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean hasEvalId = hasColumn(conn, "marks", "evaluation_id");

        if (hasEvalId) {
            String sql = "INSERT INTO marks (student_id, evaluation_id, marks_obtained) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, mark.getStudentIdAsInt());
                stmt.setInt(2, mark.getEvaluationId());
                stmt.setDouble(3, mark.getMarksObtained());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new DatabaseException("Failed to insert evaluation mark: " + e.getMessage(), e);
            }
        } else {
            // Component-based table fallback
            return upsertComponentMark(conn, mark);
        }
    }

    /**
     * Updates an existing mark entry.
     */
    public boolean updateMark(Mark mark) throws DatabaseException, SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean hasEvalId = hasColumn(conn, "marks", "evaluation_id");

        if (hasEvalId) {
            String sql = "UPDATE marks SET marks_obtained = ? WHERE student_id = ? AND evaluation_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, mark.getMarksObtained());
                stmt.setInt(2, mark.getStudentIdAsInt());
                stmt.setInt(3, mark.getEvaluationId());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new DatabaseException("Failed to update evaluation mark: " + e.getMessage(), e);
            }
        } else {
            return upsertComponentMark(conn, mark);
        }
    }

    /**
     * Helper to upsert component marks when schema has (quiz_marks, assignment_marks, etc.).
     */
    private boolean upsertComponentMark(Connection conn, Mark mark) throws SQLException {
        int studentId = mark.getStudentIdAsInt();
        int courseId = mark.getCourseId() > 0 ? mark.getCourseId() : 1;

        String checkSql = "SELECT mark_id FROM marks WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, studentId);
            checkStmt.setInt(2, courseId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String updateSql = "UPDATE marks SET quiz_marks = COALESCE(?, quiz_marks), " +
                            "assignment_marks = COALESCE(?, assignment_marks), " +
                            "mid_sem_marks = COALESCE(?, mid_sem_marks), " +
                            "practical_marks = COALESCE(?, practical_marks), " +
                            "final_exam_marks = COALESCE(?, final_exam_marks) " +
                            "WHERE student_id = ? AND course_id = ?";
                    try (PreparedStatement uStmt = conn.prepareStatement(updateSql)) {
                        setNullableDouble(uStmt, 1, mark.getQuizMarks());
                        setNullableDouble(uStmt, 2, mark.getAssignmentMarks());
                        setNullableDouble(uStmt, 3, mark.getMidSemMarks());
                        setNullableDouble(uStmt, 4, mark.getPracticalMarks());
                        setNullableDouble(uStmt, 5, mark.getFinalExamMarks());
                        uStmt.setInt(6, studentId);
                        uStmt.setInt(7, courseId);
                        return uStmt.executeUpdate() > 0;
                    }
                } else {
                    String insertSql = "INSERT INTO marks (student_id, course_id, quiz_marks, assignment_marks, mid_sem_marks, practical_marks, final_exam_marks) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement iStmt = conn.prepareStatement(insertSql)) {
                        iStmt.setInt(1, studentId);
                        iStmt.setInt(2, courseId);
                        setNullableDouble(iStmt, 3, mark.getQuizMarks());
                        setNullableDouble(iStmt, 4, mark.getAssignmentMarks());
                        setNullableDouble(iStmt, 5, mark.getMidSemMarks());
                        setNullableDouble(iStmt, 6, mark.getPracticalMarks());
                        setNullableDouble(iStmt, 7, mark.getFinalExamMarks());
                        return iStmt.executeUpdate() > 0;
                    }
                }
            }
        }
    }

    private void setNullableDouble(PreparedStatement stmt, int index, Double val) throws SQLException {
        if (val != null) {
            stmt.setDouble(index, val);
        } else {
            stmt.setNull(index, Types.DECIMAL);
        }
    }

    /**
     * Saves or updates full continuous assessment component marks for a student-course pair.
     * Ensures interoperability with Member 4 (Grade calculation).
     */
    public boolean saveCourseComponentMarks(int studentId, int courseId, Double quiz, Double assignment,
                                           Double midSem, Double practical, Double finalExam) throws DatabaseException, SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean hasQuizCol = hasColumn(conn, "marks", "quiz_marks");

        if (hasQuizCol) {
            Mark m = new Mark(0, studentId, courseId, quiz, assignment, midSem, practical, finalExam);
            return upsertComponentMark(conn, m);
        }
        return true;
    }

    /**
     * Fetches marks for a specific student across all evaluations with metadata.
     */
    public List<Mark> getMarksByStudent(String studentId) throws DatabaseException, SQLException, InvalidMarkException {
        List<Mark> marksList = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean hasEvalId = hasColumn(conn, "marks", "evaluation_id");

        if (hasEvalId) {
            String sql = "SELECT m.mark_id, m.student_id, m.evaluation_id, m.marks_obtained, " +
                    "e.title, e.type, e.weightage, e.max_marks, e.course_id " +
                    "FROM marks m " +
                    "LEFT JOIN evaluations e ON m.evaluation_id = e.evaluation_id " +
                    "WHERE m.student_id = ? ORDER BY m.evaluation_id ASC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Mark mark = new Mark(
                                rs.getString("student_id"),
                                rs.getInt("evaluation_id"),
                                rs.getDouble("marks_obtained")
                        );
                        mark.setCourseId(rs.getInt("course_id"));
                        mark.setEvaluationTitle(rs.getString("title"));
                        mark.setEvaluationType(rs.getString("type"));
                        double mx = rs.getDouble("max_marks");
                        mark.setMaxMarks(mx > 0 ? mx : 100.0);
                        marksList.add(mark);
                    }
                }
            } catch (SQLException e) {
                String fallbackSql = "SELECT * FROM marks WHERE student_id = ? ORDER BY evaluation_id ASC";
                try (PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {
                    stmt.setString(1, studentId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Mark mark = new Mark(
                                    rs.getString("student_id"),
                                    rs.getInt("evaluation_id"),
                                    rs.getDouble("marks_obtained")
                            );
                            marksList.add(mark);
                        }
                    }
                }
            }
        } else {
            // Read component marks
            String sql = "SELECT * FROM marks WHERE student_id = ? ORDER BY course_id ASC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Mark m = mapComponentMark(rs);
                        marksList.add(m);
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Failed to fetch student marks: " + e.getMessage(), e);
            }
        }
        return marksList;
    }

    /**
     * Fetches marks for a specific student and course, including evaluation metadata.
     */
    public List<Mark> getMarksByStudentAndCourse(String studentId, int courseId) throws DatabaseException, SQLException, InvalidMarkException {
        List<Mark> marksList = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean hasEvalId = hasColumn(conn, "marks", "evaluation_id");

        if (hasEvalId) {
            String sql = "SELECT m.mark_id, m.student_id, m.evaluation_id, m.marks_obtained, " +
                    "e.title, e.type, e.weightage, e.max_marks, e.course_id " +
                    "FROM marks m " +
                    "JOIN evaluations e ON m.evaluation_id = e.evaluation_id " +
                    "WHERE m.student_id = ? AND e.course_id = ? " +
                    "ORDER BY m.evaluation_id ASC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setInt(2, courseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Mark mark = new Mark(
                                rs.getString("student_id"),
                                rs.getInt("evaluation_id"),
                                rs.getDouble("marks_obtained")
                        );
                        mark.setCourseId(rs.getInt("course_id"));
                        mark.setEvaluationTitle(rs.getString("title"));
                        mark.setEvaluationType(rs.getString("type"));
                        double mx = rs.getDouble("max_marks");
                        mark.setMaxMarks(mx > 0 ? mx : 100.0);
                        marksList.add(mark);
                    }
                }
            } catch (SQLException ignored) {
            }

            // If empty, fallback to student's marks across evaluations
            if (marksList.isEmpty()) {
                return getMarksByStudent(studentId);
            }
        } else {
            String sql = "SELECT * FROM marks WHERE student_id = ? AND course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setInt(2, courseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        marksList.add(mapComponentMark(rs));
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Failed to fetch student marks: " + e.getMessage(), e);
            }
        }
        return marksList;
    }

    /**
     * Fetches all marks for a given evaluation ID.
     */
    public List<Mark> getMarksByEvaluation(int evaluationId) throws DatabaseException, SQLException, InvalidMarkException {
        List<Mark> marksList = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean hasEvalId = hasColumn(conn, "marks", "evaluation_id");

        if (hasEvalId) {
            String sql = "SELECT * FROM marks WHERE evaluation_id = ? ORDER BY student_id ASC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, evaluationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Mark mark = new Mark(
                                rs.getString("student_id"),
                                rs.getInt("evaluation_id"),
                                rs.getDouble("marks_obtained")
                        );
                        marksList.add(mark);
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Failed to fetch evaluation marks: " + e.getMessage(), e);
            }
        }
        return marksList;
    }

    /**
     * Fetches marks for a whole batch by Course ID (Requirement 6: Whole-batch marks & Marks by subject).
     * Supports both component-based marks and evaluation-based marks schemas, and falls back to student roster.
     */
    public List<Mark> getMarksByCourse(int courseId) throws DatabaseException, SQLException {
        java.util.Map<Integer, Mark> marksMap = new java.util.LinkedHashMap<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        // 1. Try querying component-based marks (marks with course_id)
        boolean hasCourseCol = hasColumn(conn, "marks", "course_id");
        if (hasCourseCol) {
            String sql = "SELECT m.*, u.username, u.first_name, u.last_name FROM marks m " +
                    "LEFT JOIN users u ON m.student_id = u.user_id " +
                    "WHERE m.course_id = ? ORDER BY m.student_id ASC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Mark m = mapComponentMark(rs);
                        marksMap.put(m.getStudentIdAsInt(), m);
                    }
                }
            } catch (SQLException ignored) {
            }
        }

        // 2. Also query evaluation-based marks (marks linked via evaluations table)
        boolean hasEvalId = hasColumn(conn, "marks", "evaluation_id");
        if (hasEvalId) {
            String evalSql = "SELECT m.student_id, m.evaluation_id, m.marks_obtained, e.type, e.title " +
                    "FROM marks m " +
                    "JOIN evaluations e ON m.evaluation_id = e.evaluation_id " +
                    "WHERE e.course_id = ? " +
                    "ORDER BY m.student_id ASC";
            try (PreparedStatement stmt = conn.prepareStatement(evalSql)) {
                stmt.setInt(1, courseId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int stdId = rs.getInt("student_id");
                        double score = rs.getDouble("marks_obtained");
                        String type = rs.getString("type");
                        if (type == null) type = rs.getString("title");
                        if (type == null) type = "";
                        type = type.toUpperCase();

                        Mark m = marksMap.get(stdId);
                        if (m == null) {
                            m = new Mark();
                            m.setStudentId(stdId);
                            m.setCourseId(courseId);
                            marksMap.put(stdId, m);
                        }

                        if (type.contains("QUIZ")) {
                            m.setQuizMarks(score);
                        } else if (type.contains("ASSESSMENT") || type.contains("ASSIGNMENT")) {
                            m.setAssignmentMarks(score);
                        } else if (type.contains("MID")) {
                            m.setMidSemMarks(score);
                        } else if (type.contains("PRACTICAL") || type.contains("LAB")) {
                            m.setPracticalMarks(score);
                        } else if (type.contains("FINAL") || type.contains("EXAM")) {
                            m.setFinalExamMarks(score);
                        } else {
                            if (m.getQuizMarks() == null) m.setQuizMarks(score);
                            else if (m.getAssignmentMarks() == null) m.setAssignmentMarks(score);
                        }
                    }
                }
            } catch (SQLException ignored) {
            }
        }

        // 3. If no marks recorded yet, fetch registered undergraduate students so the course roster is displayed
        if (marksMap.isEmpty()) {
            String studentSql = "SELECT u.user_id, u.username, u.first_name, u.last_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE UPPER(COALESCE(r.role_name, '')) IN ('UNDERGRADUATE', 'STUDENT') " +
                    "OR u.role_id = 4 OR u.username LIKE 'tg%' " +
                    "ORDER BY u.user_id ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(studentSql)) {
                while (rs.next()) {
                    int stdId = rs.getInt("user_id");
                    Mark m = new Mark();
                    m.setStudentId(stdId);
                    m.setCourseId(courseId);
                    marksMap.put(stdId, m);
                }
            } catch (SQLException ignored) {
            }
        }

        return new ArrayList<>(marksMap.values());
    }

    /**
     * Fetches all registered evaluations for a course.
     */
    public List<Evaluation> getEvaluationsByCourse(int courseId) throws DatabaseException, SQLException {
        List<Evaluation> evals = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean hasEvalTable = true;

        String sql = "SELECT * FROM evaluations WHERE course_id = ? ORDER BY evaluation_id ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    evals.add(new Evaluation(
                            rs.getInt("evaluation_id"),
                            String.valueOf(rs.getInt("course_id")),
                            rs.getString("title"),
                            rs.getString("type"),
                            rs.getDouble("weightage"),
                            rs.getDouble("max_marks")
                    ));
                }
            }
        } catch (SQLException e) {
            // Table evaluations might be empty or unseeded, create default components
            evals.add(new Evaluation(1, String.valueOf(courseId), "Quiz 1", "QUIZ", 10.0, 100.0));
            evals.add(new Evaluation(2, String.valueOf(courseId), "Continuous Assessment", "ASSESSMENT", 20.0, 100.0));
            evals.add(new Evaluation(3, String.valueOf(courseId), "Mid-Semester Examination", "MID_TERM", 20.0, 100.0));
            evals.add(new Evaluation(4, String.valueOf(courseId), "Practical Assessment", "PRACTICAL", 20.0, 100.0));
            evals.add(new Evaluation(5, String.valueOf(courseId), "Final Examination", "FINAL", 30.0, 100.0));
        }
        return evals;
    }

    private Mark mapComponentMark(ResultSet rs) throws SQLException {
        Mark m = new Mark();
        m.setMarkId(rs.getInt("mark_id"));
        m.setStudentId(rs.getInt("student_id"));
        m.setCourseId(rs.getInt("course_id"));
        m.setQuizMarks(getDoubleOrNull(rs, "quiz_marks"));
        m.setAssignmentMarks(getDoubleOrNull(rs, "assignment_marks"));
        m.setMidSemMarks(getDoubleOrNull(rs, "mid_sem_marks"));
        m.setPracticalMarks(getDoubleOrNull(rs, "practical_marks"));
        m.setFinalExamMarks(getDoubleOrNull(rs, "final_exam_marks"));
        return m;
    }

    private Double getDoubleOrNull(ResultSet rs, String col) throws SQLException {
        try {
            double v = rs.getDouble(col);
            return rs.wasNull() ? null : v;
        } catch (SQLException ignored) {
            return null;
        }
    }

    /**
     * Deletes a student mark entry.
     */
    public boolean deleteMark(String studentId, int evaluationId) throws DatabaseException, SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        boolean hasEvalId = hasColumn(conn, "marks", "evaluation_id");

        if (hasEvalId) {
            String sql = "DELETE FROM marks WHERE student_id = ? AND evaluation_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setInt(2, evaluationId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new DatabaseException("Failed to delete mark: " + e.getMessage(), e);
            }
        } else {
            String sql = "DELETE FROM marks WHERE student_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new DatabaseException("Failed to delete mark: " + e.getMessage(), e);
            }
        }
    }
}