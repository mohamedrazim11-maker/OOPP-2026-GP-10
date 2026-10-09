package com.faculty.management.model;

/**
 * Represents a Lecturer user.
 * Demonstrates Inheritance from User.
 */
public class Lecturer extends User {
    //specific field
    private String department;

    public Lecturer() {
        super();
    }

    public Lecturer(int userId, String username, String password, String email, 
                    String firstName, String lastName, Role role, String contactNo, 
                    String profilePic, String status) {
        super(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRoleTitle() {
        return "Lecturer";
    }

    @Override
    public String getDashboardGreeting() {
        return "Welcome to Lecturer Portal, " + getFullName() + "!";
    }
}
