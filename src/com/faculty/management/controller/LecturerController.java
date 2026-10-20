package com.faculty.management.controller;

import com.faculty.management.model.CourseMaterial;
import com.faculty.management.model.Lecturer;
import com.faculty.management.service.CourseMaterialService;
import com.faculty.management.service.LecturerService;

import java.io.File;
import java.util.List;

public class LecturerController {
    private final LecturerService lecturerService = new LecturerService();
    private final CourseMaterialService materialService = new CourseMaterialService();

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

    public boolean uploadCourseMaterial(String courseId, String title, File file) {
        try {
            return materialService.uploadMaterial(courseId, title, file);
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
}
