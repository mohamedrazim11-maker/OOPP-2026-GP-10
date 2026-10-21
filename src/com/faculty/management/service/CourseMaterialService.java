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

    public boolean uploadMaterial(String courseId, String title, File sourceFile) throws IOException, SQLException {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String destFileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        File destFile = new File(dir, destFileName);
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        CourseMaterial material = new CourseMaterial(0, courseId, title, destFile.getPath(), null);
        try {
            return materialDAO.saveMaterial(material);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CourseMaterial> fetchMaterials(String courseId) throws SQLException {
        try {
            return materialDAO.getMaterialsByCourse(courseId);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}