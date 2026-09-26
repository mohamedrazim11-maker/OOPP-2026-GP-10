package com.faculty.management.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password verification and hashing.
 * Supports both plain-text comparison (for seeded demo data) and SHA-256 hashing.
 */
public class PasswordUtil {

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Verifies plain password against stored password (direct match or hash match).
     */
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        // Direct match
        if (inputPassword.equals(storedPassword)) {
            return true;
        }
        // Hash match
        String hashedInput = hashPassword(inputPassword);
        return hashedInput != null && hashedInput.equalsIgnoreCase(storedPassword);
    }

    /**
     * Hashes a password using SHA-256.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }
}
