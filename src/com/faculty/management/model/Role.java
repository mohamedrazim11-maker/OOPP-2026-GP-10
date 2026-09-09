package com.faculty.management.model;

/**
 * Represents a User Role in the system.
 */
public class Role {
    private int roleId;
    private String roleName;
    private String description;

    public static final String ADMIN = "ADMIN";
    public static final String LECTURER = "LECTURER";
    public static final String TECHNICAL_OFFICER = "TECHNICAL_OFFICER";
    public static final String UNDERGRADUATE = "UNDERGRADUATE";

    public Role() {
    }

    public Role(int roleId, String roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
