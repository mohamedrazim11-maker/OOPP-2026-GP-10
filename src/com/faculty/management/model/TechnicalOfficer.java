package com.faculty.management.model;

/**
 * Represents a Technical Officer user.
 * Demonstrates Inheritance from User.
 */
public class TechnicalOfficer extends User {

    private String department;

    public TechnicalOfficer() {
        super();
    }

    public TechnicalOfficer(int userId, String username, String password, String email, 
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
        return "Technical Officer";
    }

    @Override
    public String getDashboardGreeting() {
        return "Welcome to Technical Officer Portal, " + getFullName() + "!";
    }
}
