package com.faculty.management.model;

/**
 * Represents an Administrator user.
 * Demonstrates Inheritance from User.
 */
public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(int userId, String username, String password, String email, 
                 String firstName, String lastName, Role role, String contactNo, 
                 String profilePic, String status) {
        super(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
    }

    @Override
    public String getRoleTitle() {
        return "System Administrator";
    }

    @Override
    public String getDashboardGreeting() {
        return "Welcome to Admin Portal, " + getFullName() + "!";
    }
}
