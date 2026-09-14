package com.faculty.management.service;

import com.faculty.management.config.DatabaseConnection;
import com.faculty.management.dao.RoleDAO;
import com.faculty.management.dao.UserDAO;
import com.faculty.management.exception.DatabaseException;
import com.faculty.management.exception.ValidationException;
import com.faculty.management.model.*;
import com.faculty.management.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service layer handling user business logic, validations, and database persistence.
 * Demonstrates Classes & Objects, Encapsulation, Abstraction, Polymorphism, 
 * Error/Exception Handling, and Database Handling.
 */
public class UserService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    // In-memory mock storage for fallback demo mode when database is not running
    private static final List<User> mockUsers = new ArrayList<>();
    private static final AtomicInteger mockIdGenerator = new AtomicInteger(100);

    static {
        initMockData();
    }

    public UserService() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
    }

    /**
     * Creates a new user profile after performing business validations and database persistence.
     *
     * @param user            The concrete User subclass instance (Admin, Lecturer, TechnicalOfficer, Undergraduate)
     * @param rawPassword     Plain password string
     * @param confirmPassword Confirmation password string
     * @return The created User instance with its assigned ID
     * @throws ValidationException If validation rules fail (empty fields, format, duplicate username/email)
     * @throws DatabaseException   If database operations fail
     */
    public User createUser(User user, String rawPassword, String confirmPassword) 
            throws ValidationException, DatabaseException {

        // 1. Validate fields using ValidationUtil (Encapsulation & Exception Handling)
        ValidationUtil.validateNewUser(user, rawPassword, confirmPassword);

        // Ensure user password property is set
        user.setPassword(rawPassword);

        // 2. Resolve Role ID if missing
        if (user.getRole() != null) {
            String roleName = user.getRole().getRoleName();
            int resolvedRoleId = mapRoleNameToId(roleName);
            user.getRole().setRoleId(resolvedRoleId);
        }

        // 3. Database persistence when connected
        if (DatabaseConnection.getInstance().isConnected()) {
            // Check username uniqueness in DB
            if (userDAO.existsByUsername(user.getUsername())) {
                throw new ValidationException("Username '" + user.getUsername() + "' is already taken. Please choose another.");
            }

            // Check email uniqueness in DB
            if (userDAO.existsByEmail(user.getEmail())) {
                throw new ValidationException("Email '" + user.getEmail() + "' is already registered. Please choose another.");
            }

            // Persist to database (Database Handling & Polymorphism)
            User createdUser = userDAO.createUser(user);

            // Also keep mock copy in sync for offline consistency
            mockUsers.add(createdUser);

            return createdUser;
        }

        // 4. Offline Fallback Demo Mode (when MySQL is not active)
        for (User u : mockUsers) {
            if (u.getUsername().equalsIgnoreCase(user.getUsername())) {
                throw new ValidationException("Username '" + user.getUsername() + "' already exists in the system.");
            }
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new ValidationException("Email '" + user.getEmail() + "' already exists in the system.");
            }
        }

        user.setUserId(mockIdGenerator.incrementAndGet());
        mockUsers.add(user);
        return user;
    }

    /**
     * Retrieves all user profiles polymorphically.
     *
     * @return List of User objects
     * @throws DatabaseException If database error occurs
     */
    public List<User> getAllUsers() throws DatabaseException {
        if (DatabaseConnection.getInstance().isConnected()) {
            return userDAO.getAllUsers();
        }
        // Fallback to in-memory mock users
        return new ArrayList<>(mockUsers);
    }

    /**
     * Finds a user by their unique ID.
     *
     * @param userId ID of the user
     * @return User object or null
     * @throws DatabaseException If database error occurs
     */
    public User getUserById(int userId) throws DatabaseException {
        if (DatabaseConnection.getInstance().isConnected()) {
            return userDAO.findById(userId);
        }
        for (User u : mockUsers) {
            if (u.getUserId() == userId) {
                return u;
            }
        }
        return null;
    }

    /**
     * Finds a user by their username.
     *
     * @param username Username of the user
     * @return User object or null if not found
     * @throws DatabaseException If database error occurs
     */
    public User getUserByUsername(String username) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        if (DatabaseConnection.getInstance().isConnected()) {
            return userDAO.findByUsername(username.trim());
        }
        for (User u : mockUsers) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) {
                return u;
            }
        }
        return null;
    }

    /**
     * Updates an existing user's details with business rule validations and DB persistence.
     *
     * @param user User object containing updated details
     * @return true if updated successfully
     * @throws ValidationException If validation fails (empty fields, format, duplicate email)
     * @throws DatabaseException   If database error occurs
     */
    public boolean updateUser(User user) throws ValidationException, DatabaseException {
        // 1. Validate fields
        ValidationUtil.validateUserUpdate(user);

        // 2. Resolve Role ID
        if (user.getRole() != null) {
            String roleName = user.getRole().getRoleName();
            int resolvedRoleId = mapRoleNameToId(roleName);
            user.getRole().setRoleId(resolvedRoleId);
        }

        // 3. Database Persistence
        if (DatabaseConnection.getInstance().isConnected()) {
            // Check if updated email is already taken by another user
            if (userDAO.existsByEmailExcludingUser(user.getEmail(), user.getUserId())) {
                throw new ValidationException("Email '" + user.getEmail() + "' is already in use by another account.");
            }

            boolean updated = userDAO.updateUser(user);
            updateMockUser(user);
            return updated;
        }

        // 4. Offline Fallback Demo Mode
        for (User u : mockUsers) {
            if (u.getUserId() != user.getUserId() && u.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new ValidationException("Email '" + user.getEmail() + "' is already in use by another account.");
            }
        }

        return updateMockUser(user);
    }

    /**
     * Updates user credentials (username and password).
     *
     * @param userId      ID of user
     * @param newUsername New username
     * @param newPassword New password
     * @return true if updated
     * @throws ValidationException If credentials invalid
     * @throws DatabaseException   If database error occurs
     */
    public boolean updateCredentials(int userId, String newUsername, String newPassword) 
            throws ValidationException, DatabaseException {

        if (ValidationUtil.isEmpty(newUsername) || newUsername.trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters.");
        }
        if (ValidationUtil.isEmpty(newPassword) || newPassword.trim().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters.");
        }

        if (DatabaseConnection.getInstance().isConnected()) {
            if (userDAO.existsByUsernameExcludingUser(newUsername.trim(), userId)) {
                throw new ValidationException("Username '" + newUsername + "' is already taken.");
            }
            return userDAO.updateCredentials(userId, newUsername.trim(), newPassword.trim());
        }

        for (User u : mockUsers) {
            if (u.getUserId() != userId && u.getUsername().equalsIgnoreCase(newUsername.trim())) {
                throw new ValidationException("Username '" + newUsername + "' is already taken.");
            }
        }

        for (User u : mockUsers) {
            if (u.getUserId() == userId) {
                u.setUsername(newUsername.trim());
                u.setPassword(newPassword.trim());
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the active/inactive status of a user.
     *
     * @param userId ID of the user
     * @param status New status ("ACTIVE" or "INACTIVE")
     * @return true if updated
     * @throws DatabaseException If database error occurs
     */
    public boolean updateStatus(int userId, String status) throws DatabaseException {
        if (DatabaseConnection.getInstance().isConnected()) {
            return userDAO.updateStatus(userId, status);
        }

        for (User u : mockUsers) {
            if (u.getUserId() == userId) {
                u.setStatus(status);
                return true;
            }
        }
        return false;
    }

    /**
     * Assigns a new role to an existing user with validation and database handling.
     *
     * @param userId   ID of the user
     * @param roleName Target role name (e.g. "Admin", "Lecturer", "Technical Officer", "Undergraduate")
     * @return true if updated successfully
     * @throws ValidationException If validation fails (e.g., demoting root admin or invalid role)
     * @throws DatabaseException   If database error occurs
     */
    public boolean assignRole(int userId, String roleName) throws ValidationException, DatabaseException {
        User user = getUserById(userId);
        if (user == null) {
            throw new ValidationException("User with ID " + userId + " was not found in the system.");
        }

        ValidationUtil.validateRoleAssignment(userId, user.getUsername(), roleName);

        int resolvedRoleId = mapRoleNameToId(roleName);
        Role newRole = new Role(resolvedRoleId, roleName.toUpperCase().replace(" ", "_"), roleName);

        if (DatabaseConnection.getInstance().isConnected()) {
            boolean updated = userDAO.updateUserRole(userId, resolvedRoleId);
            updateMockUserRole(userId, newRole);
            return updated;
        }

        return updateMockUserRole(userId, newRole);
    }

    /**
     * Polymorphic overload to assign a role using User and Role entity instances.
     *
     * @param user    Target User entity
     * @param newRole New Role entity
     * @return true if updated
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean assignRole(User user, Role newRole) throws ValidationException, DatabaseException {
        if (user == null) {
            throw new ValidationException("User entity cannot be null.");
        }
        if (newRole == null || ValidationUtil.isEmpty(newRole.getRoleName())) {
            throw new ValidationException("Role entity must contain a valid role name.");
        }
        return assignRole(user.getUserId(), newRole.getRoleName());
    }

    private boolean updateMockUserRole(int userId, Role newRole) {
        for (User u : mockUsers) {
            if (u.getUserId() == userId) {
                u.setRole(newRole);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a user by ID after verifying business rules and authorization.
     * Prevents deletion of the primary system administrator and invalid IDs.
     *
     * @param userId ID of the user to delete
     * @return true if deleted successfully
     * @throws ValidationException If validation rules fail (e.g., deleting root admin or invalid ID)
     * @throws DatabaseException   If database error occurs
     */
    public boolean deleteUser(int userId) throws ValidationException, DatabaseException {
        // 1. Fetch user to verify existence and username
        User user = getUserById(userId);
        if (user == null) {
            throw new ValidationException("User with ID " + userId + " does not exist.");
        }

        // 2. Validate deletion rules (Encapsulation & Exception Handling)
        ValidationUtil.validateUserDeletion(userId, user.getUsername());

        // 3. Database persistence
        if (DatabaseConnection.getInstance().isConnected()) {
            boolean deleted = userDAO.deleteUser(userId);
            mockUsers.removeIf(u -> u.getUserId() == userId);
            return deleted;
        }

        // 4. Offline Fallback Demo Mode
        return mockUsers.removeIf(u -> u.getUserId() == userId);
    }

    /**
     * Polymorphic overload to delete a user directly using a User entity.
     *
     * @param user The User instance to delete
     * @return true if deleted successfully
     * @throws ValidationException If validation fails
     * @throws DatabaseException   If database error occurs
     */
    public boolean deleteUser(User user) throws ValidationException, DatabaseException {
        if (user == null) {
            throw new ValidationException("User cannot be null.");
        }
        return deleteUser(user.getUserId());
    }

    /**
     * Retrieves all roles available in the system.
     *
     * @return List of Role objects
     * @throws DatabaseException If database error occurs
     */
    public List<Role> getAllRoles() throws DatabaseException {
        if (DatabaseConnection.getInstance().isConnected()) {
            return roleDAO.getAllRoles();
        }

        List<Role> roles = new ArrayList<>();
        roles.add(new Role(1, Role.ADMIN, "System Administrator"));
        roles.add(new Role(2, Role.LECTURER, "Lecturer"));
        roles.add(new Role(3, Role.TECHNICAL_OFFICER, "Technical Officer"));
        roles.add(new Role(4, Role.UNDERGRADUATE, "Undergraduate Student"));
        return roles;
    }

    private boolean updateMockUser(User user) {
        for (int i = 0; i < mockUsers.size(); i++) {
            if (mockUsers.get(i).getUserId() == user.getUserId()) {
                User existing = mockUsers.get(i);
                existing.setFirstName(user.getFirstName());
                existing.setLastName(user.getLastName());
                existing.setEmail(user.getEmail());
                existing.setContactNo(user.getContactNo());
                existing.setRole(user.getRole());
                existing.setStatus(user.getStatus());
                if (user instanceof Undergraduate && existing instanceof Undergraduate) {
                    ((Undergraduate) existing).setDepartment(((Undergraduate) user).getDepartment());
                    ((Undergraduate) existing).setBatch(((Undergraduate) user).getBatch());
                } else if (user instanceof Lecturer && existing instanceof Lecturer) {
                    ((Lecturer) existing).setDepartment(((Lecturer) user).getDepartment());
                } else if (user instanceof TechnicalOfficer && existing instanceof TechnicalOfficer) {
                    ((TechnicalOfficer) existing).setDepartment(((TechnicalOfficer) user).getDepartment());
                }
                return true;
            }
        }
        return false;
    }

    private int mapRoleNameToId(String roleName) {
        if (roleName == null) return 4;
        String normalized = roleName.trim().toUpperCase().replace(" ", "_");
        switch (normalized) {
            case "ADMIN":
            case "SYSTEM_ADMINISTRATOR":
                return 1;
            case "LECTURER":
                return 2;
            case "TECHNICAL_OFFICER":
            case "TECHNICALOFFICER":
                return 3;
            case "UNDERGRADUATE":
            case "STUDENT":
            default:
                return 4;
        }
    }

    /**
     * Initializes default mock data matching req.txt (Admin, 5 Lecturers, 4 TOs, 5 Students).
     */
    private static void initMockData() {
        Role adminRole = new Role(1, Role.ADMIN, "System Administrator");
        Role lecRole = new Role(2, Role.LECTURER, "Lecturer");
        Role toRole = new Role(3, Role.TECHNICAL_OFFICER, "Technical Officer");
        Role stdRole = new Role(4, Role.UNDERGRADUATE, "Undergraduate Student");

        // 1 Admin
        mockUsers.add(new Admin(1, "admin", "admin123", "admin@fot.ruh.ac.lk", "Admin", "User", adminRole, "0711234567", null, "ACTIVE"));

        // 5 Lecturers
        mockUsers.add(new Lecturer(2, "lec_kamal", "lec123", "kamal@fot.ruh.ac.lk", "Kamal", "Perera", lecRole, "0771122334", null, "ACTIVE"));
        mockUsers.add(new Lecturer(3, "lec_nimal", "lec123", "nimal@fot.ruh.ac.lk", "Nimal", "Silva", lecRole, "0772233445", null, "ACTIVE"));
        mockUsers.add(new Lecturer(4, "lec_sunil", "lec123", "sunil@fot.ruh.ac.lk", "Sunil", "Fernando", lecRole, "0773344556", null, "ACTIVE"));
        mockUsers.add(new Lecturer(5, "lec_amara", "lec123", "amara@fot.ruh.ac.lk", "Amara", "Jayasinghe", lecRole, "0774455667", null, "ACTIVE"));
        mockUsers.add(new Lecturer(6, "lec_champa", "lec123", "champa@fot.ruh.ac.lk", "Champa", "Wickramasinghe", lecRole, "0775566778", null, "ACTIVE"));

        // 4 Technical Officers
        mockUsers.add(new TechnicalOfficer(7, "to_bandara", "to123", "bandara@fot.ruh.ac.lk", "Bandara", "Herath", toRole, "0761122334", null, "ACTIVE"));
        mockUsers.add(new TechnicalOfficer(8, "to_sarath", "to123", "sarath@fot.ruh.ac.lk", "Sarath", "Kumara", toRole, "0762233445", null, "ACTIVE"));
        mockUsers.add(new TechnicalOfficer(9, "to_anura", "to123", "anura@fot.ruh.ac.lk", "Anura", "Dissanayake", toRole, "0763344556", null, "ACTIVE"));
        mockUsers.add(new TechnicalOfficer(10, "to_kasun", "to123", "kasun@fot.ruh.ac.lk", "Kasun", "Gunawardena", toRole, "0764455667", null, "ACTIVE"));

        // Undergraduates
        mockUsers.add(new Undergraduate(11, "tg2021001", "std123", "tg2021001@fot.ruh.ac.lk", "Kasun", "Kalhara", stdRole, "0701122334", null, "ACTIVE"));
        mockUsers.add(new Undergraduate(12, "tg2021002", "std123", "tg2021002@fot.ruh.ac.lk", "Nuwan", "Pradeep", stdRole, "0702233445", null, "ACTIVE"));
        mockUsers.add(new Undergraduate(13, "tg2021003", "std123", "tg2021003@fot.ruh.ac.lk", "Dinuka", "Madushan", stdRole, "0703344556", null, "ACTIVE"));
        mockUsers.add(new Undergraduate(14, "tg2021004", "std123", "tg2021004@fot.ruh.ac.lk", "Sajith", "Premadasa", stdRole, "0704455667", null, "ACTIVE"));
        mockUsers.add(new Undergraduate(15, "tg2021005", "std123", "tg2021005@fot.ruh.ac.lk", "Ishara", "Sandaruwan", stdRole, "0705566778", null, "ACTIVE"));
    }
}
