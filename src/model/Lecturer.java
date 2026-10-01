package model;

// Demonstrates Inheritance: Lecturer extends User
public class
Lecturer extends User {
    private String department;
    private String contactNo;
    private String profilePicPath;

    public Lecturer(String userId, String name, String email, String department, String contactNo, String profilePicPath) {
        super(userId, name, email, "LECTURER");
        this.department = department;
        this.contactNo = contactNo;
        this.profilePicPath = profilePicPath;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getProfilePicPath() { return profilePicPath; }
    public void setProfilePicPath(String profilePicPath) { this.profilePicPath = profilePicPath; }
}
