package com.faculty.management.model;

/**
 * Represents raw component marks entered for a Student in a Course.
 * Shared reference entity — primarily owned/uploaded by Member 2
 * (Lecturer & Academic Management). Member 4 reads this data to
 * compute the CA percentage, final total, grade, and GPA.
 *
 * All component marks are out of 100. CA% is the average of the
 * components that were actually entered (quiz, assignment, mid-semester,
 * practical). The final total mark uses a documented 40:60 weighting
 * of CA vs. the final examination (see GradeCalculator).
 */
public class Mark {

    private int markId;
    private int studentId;
    private int courseId;
    private Double quizMarks;
    private Double assignmentMarks;
    private Double midSemMarks;
    private Double practicalMarks;
    private Double finalExamMarks;

    public Mark() {
    }

    public Mark(int markId, int studentId, int courseId, Double quizMarks, Double assignmentMarks,
                Double midSemMarks, Double practicalMarks, Double finalExamMarks) {
        this.markId = markId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.quizMarks = quizMarks;
        this.assignmentMarks = assignmentMarks;
        this.midSemMarks = midSemMarks;
        this.practicalMarks = practicalMarks;
        this.finalExamMarks = finalExamMarks;
    }

    /**
     * Computes the Continuous Assessment percentage as the average of
     * whichever CA components (quiz / assignment / mid-semester / practical)
     * were actually recorded for this student-course pair.
     */
    public double computeCaMarks() {
        double sum = 0;
        int count = 0;
        if (quizMarks != null) {
            sum += quizMarks;
            count++;
        }
        if (assignmentMarks != null) {
            sum += assignmentMarks;
            count++;
        }
        if (midSemMarks != null) {
            sum += midSemMarks;
            count++;
        }
        if (practicalMarks != null) {
            sum += practicalMarks;
            count++;
        }
        return count == 0 ? 0.0 : sum / count;
    }

    public double getFinalExamOrZero() {
        return finalExamMarks == null ? 0.0 : finalExamMarks;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Double getQuizMarks() {
        return quizMarks;
    }

    public void setQuizMarks(Double quizMarks) {
        this.quizMarks = quizMarks;
    }

    public Double getAssignmentMarks() {
        return assignmentMarks;
    }

    public void setAssignmentMarks(Double assignmentMarks) {
        this.assignmentMarks = assignmentMarks;
    }

    public Double getMidSemMarks() {
        return midSemMarks;
    }

    public void setMidSemMarks(Double midSemMarks) {
        this.midSemMarks = midSemMarks;
    }

    public Double getPracticalMarks() {
        return practicalMarks;
    }

    public void setPracticalMarks(Double practicalMarks) {
        this.practicalMarks = practicalMarks;
    }

    public Double getFinalExamMarks() {
        return finalExamMarks;
    }

    public void setFinalExamMarks(Double finalExamMarks) {
        this.finalExamMarks = finalExamMarks;
    }
}
