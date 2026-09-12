package com.faculty.management.model;

/**
 * Represents an Undergraduate (Student) user.
 * Demonstrates Inheritance from User.
 */
public class Undergraduate extends User {

    private String batch;
    private String department;

    public Undergraduate() {
        super();
    }

    public Undergraduate(int userId, String username, String password, String email, 
                         String firstName, String lastName, Role role, String contactNo, 
                         String profilePic, String status) {
        super(userId, username, password, email, firstName, lastName, role, contactNo, profilePic, status);
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRoleTitle() {
        return "Undergraduate Student";
    }

    @Override
    public String getDashboardGreeting() {
        return "Welcome to Student Portal, " + getFullName() + "!";
    }
}
