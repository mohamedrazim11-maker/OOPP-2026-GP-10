package com.faculty.management.service;

import com.faculty.management.dao.CourseMaterialDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.model.CourseMaterial;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class CourseMaterialService {
    private final CourseMaterialDAO materialDAO = new CourseMaterialDAO();
    private static final String UPLOAD_DIR = "resources/course_materials/";

    public boolean uploadMaterial(String courseId, String title, File sourceFile, int lecturerId) throws IOException, SQLException, DatabaseException {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String destFileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        File destFile = new File(dir, destFileName);
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        CourseMaterial material = new CourseMaterial(0, courseId, lecturerId, title, destFile.getPath(), null);
        return materialDAO.saveMaterial(material);
    }

    public boolean uploadMaterial(String courseId, String title, File sourceFile) throws IOException, SQLException, DatabaseException {
        return uploadMaterial(courseId, title, sourceFile, 2);
    }

    public boolean modifyMaterial(int materialId, String courseId, String title, File newFile) throws IOException, SQLException, DatabaseException {
        String path = null;
        if (newFile != null) {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String destFileName = System.currentTimeMillis() + "_" + newFile.getName();
            File destFile = new File(dir, destFileName);
            Files.copy(newFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            path = destFile.getPath();
        }

        CourseMaterial material = new CourseMaterial(materialId, courseId, title, path, null);
        return materialDAO.updateMaterial(material);
    }

    public boolean deleteMaterial(int materialId) throws SQLException, DatabaseException {
        return materialDAO.deleteMaterial(materialId);
    }

    public List<CourseMaterial> fetchMaterials(String courseId) throws SQLException, DatabaseException {
        return materialDAO.getMaterialsByCourse(courseId);
    }
}