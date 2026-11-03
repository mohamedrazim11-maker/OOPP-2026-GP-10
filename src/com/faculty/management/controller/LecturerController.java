package com.faculty.management.controller;

<<<<<<< HEAD
import com.faculty.management.model.Course;
import com.faculty.management.model.CourseMaterial;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.Mark;
import com.faculty.management.service.CourseMaterialService;
=======
import com.faculty.management.model.CourseMaterial;
import com.faculty.management.model.Eligibility;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.Mark;
import com.faculty.management.service.CourseMaterialService;
import com.faculty.management.service.EligibilityService;
>>>>>>> Aysha
import com.faculty.management.service.LecturerService;
import com.faculty.management.service.MarkService;

import java.io.File;
<<<<<<< HEAD
import java.util.List;

/**
 * Controller mediating GUI actions and Services for Member 2 (Lecturer & Academic Management).
 */
public class LecturerController {
    private final LecturerService lecturerService = new LecturerService();
    private final CourseMaterialService materialService = new CourseMaterialService();
    private final MarkService markService = new MarkService();

    // --- Profile Management ---
=======
import java.util.Collections;
import java.util.List;

/**
 * Controller class acting as a Facade for Member 2 (Lecturer Module).
 * Integrates Profile, Materials, Marks Entry, and CA Eligibility logic.
 */
public class LecturerController {

    private final LecturerService lecturerService = new LecturerService();
    private final CourseMaterialService materialService = new CourseMaterialService();
    private final MarkService markService = new MarkService();
    private final EligibilityService eligibilityService = new EligibilityService();

    // --- Profile Management (Existing) ---
>>>>>>> Aysha
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

<<<<<<< HEAD
    public List<Course> getAssignedCourses(int lecturerId) {
        try {
            return lecturerService.getAssignedCourses(lecturerId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // --- Course Materials ---
=======
    // --- Course Materials (Existing) ---
>>>>>>> Aysha
    public boolean uploadCourseMaterial(String courseId, String title, File file) {
        try {
            return materialService.uploadMaterial(courseId, title, file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

<<<<<<< HEAD
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

=======
>>>>>>> Aysha
    public List<CourseMaterial> getCourseMaterials(String courseId) {
        try {
            return materialService.fetchMaterials(courseId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

<<<<<<< HEAD
    // --- Marks Management ---
=======
    // --- Marks Management (Week 3 Addition) ---
>>>>>>> Aysha
    public boolean saveOrUpdateMark(String studentId, int evaluationId, double marks) {
        try {
            return markService.saveOrUpdateMark(studentId, evaluationId, marks);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

<<<<<<< HEAD
    public boolean deleteMark(String studentId, int evaluationId) {
        try {
            return markService.removeMark(studentId, evaluationId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

=======
>>>>>>> Aysha
    public List<Mark> getStudentMarks(String studentId) {
        try {
            return markService.getMarksForStudent(studentId);
        } catch (Exception e) {
            e.printStackTrace();
<<<<<<< HEAD
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
=======
            return Collections.emptyList();
        }
    }

    public List<Mark> getEvaluationMarks(int evaluationId) {
        try {
            return markService.getMarksForEvaluation(evaluationId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean removeMark(String studentId, int evaluationId) {
        try {
            return markService.removeMark(studentId, evaluationId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- CA & Eligibility (Week 4 Addition) ---
    public Eligibility processCAEligibility(int studentId, int courseId, double totalObtained, double totalMax) {
        try {
            return eligibilityService.calculateAndSaveEligibility(studentId, courseId, totalObtained, totalMax);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Eligibility> getBatchEligibility(Integer courseFilter, String statusFilter) {
        try {
            return eligibilityService.getBatchEligibility(courseFilter, statusFilter);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
>>>>>>> Aysha
