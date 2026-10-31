package com.faculty.management.controller;

import com.faculty.management.model.Course;
import com.faculty.management.model.CourseMaterial;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.Mark;
import com.faculty.management.service.CourseMaterialService;
import com.faculty.management.service.LecturerService;
import com.faculty.management.service.MarkService;

import java.io.File;
import java.util.List;

/**
 * Controller mediating GUI actions and Services for Member 2 (Lecturer & Academic Management).
 */
public class LecturerController {
    private final LecturerService lecturerService = new LecturerService();
    private final CourseMaterialService materialService = new CourseMaterialService();
    private final MarkService markService = new MarkService();

    // --- Profile Management ---
    public boolean updateProfile(Lecturer lecturer) {
        try {
            return lecturerService.updateProfile(lecturer);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Lecturer getProfile(int userId) {
        try {
            return lecturerService.getProfile(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Course> getAssignedCourses(int lecturerId) {
        try {
            return lecturerService.getAssignedCourses(lecturerId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // --- Course Materials ---
    public boolean uploadCourseMaterial(String courseId, String title, File file) {
        try {
            return materialService.uploadMaterial(courseId, title, file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifyCourseMaterial(int materialId, String courseId, String title, File newFile) {
        try {
            return materialService.modifyMaterial(materialId, courseId, title, newFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCourseMaterial(int materialId) {
        try {
            return materialService.deleteMaterial(materialId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CourseMaterial> getCourseMaterials(String courseId) {
        try {
            return materialService.fetchMaterials(courseId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // --- Marks Management ---
    public boolean saveOrUpdateMark(String studentId, int evaluationId, double marks) {
        try {
            return markService.saveOrUpdateMark(studentId, evaluationId, marks);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMark(String studentId, int evaluationId) {
        try {
            return markService.removeMark(studentId, evaluationId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Mark> getStudentMarks(String studentId) {
        try {
            return markService.getMarksForStudent(studentId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Mark> getCourseMarks(int courseId) {
        try {
            return markService.getMarksForCourse(courseId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
