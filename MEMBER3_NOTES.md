# Member 3 — Technical Officer, Attendance & Medical Records Module

### ICT2132 – Object Oriented Programming Practicum | Mini Project
**Student Name:** Hansika Munaweera  
**Assigned Role:** Member 3 — Technical Officer (Attendance, Medicals, Department Timetables & Notices)  
**Active Branch:** `hansika`  
**Repository:** https://github.com/mohamedrazim11-maker/OOPP-2026-GP-10.git  

---

## 1. Module Overview & Architectural Layers

Member 3 is responsible for operational academic administration: recording and monitoring daily undergraduate attendance (Theory & Practical), verifying and managing medical certificates for excused absences, displaying departmental timetables and lab facility allocations, and providing access to faculty notices.

| Layer | Implemented Files |
| :--- | :--- |
| **Model** | `TechnicalOfficer.java`, `Attendance.java`, `AttendanceSession.java`, `AttendanceRecord.java`, `Medical.java`, `StudentAttendanceSummary.java` |
| **DAO** | `AttendanceDAO.java`, `MedicalDAO.java`, `NoticeDAO.java` (integration), `TimetableDAO.java` (integration), `UserDAO.java` (integration) |
| **GUI** | `TechnicalOfficerDashboard.java` (3x2 operations shell), `AttendanceEntryDialog.java`, `ViewAttendanceDialog.java`, `MedicalManagementDialog.java`, `DepartmentTimetableDialog.java`, `NoticeViewerDialog.java`, `TechnicalOfficerProfileDialog.java` |
| **Database** | `database/02_create_tables.sql` (tables 4, 5, 6), `database/06_insert_attendance_medical.sql` (15 sessions + 5 test scenarios) |

---

## 2. Core Business Rules & Implemented Features

### 1. Daily Session Attendance Tracking (15 Sessions)
* Supports **15 Theory Sessions** and **15 Practical Sessions** per course module.
* Session credit weighting: $1 \text{ Theory Credit (1T)} = 2 \text{ hours}$, $1 \text{ Practical Credit (1P)} = 2 \text{ hours}$.
* Allows selecting Session Number (1 to 15), Session Type, Date, Course, and marks students as `PRESENT` or `ABSENT`.
* Provides real-time student keyword search, "Mark All Present", "Mark All Absent", session record updates, and deletion.

### 2. 80% UGC Examination Eligibility Formula & Multi-Component Evaluation
* Adheres to the Sri Lankan University Grants Commission mandatory **80% attendance rule**:
  $$\text{Attendance \%} = \left( \frac{\text{Attended Sessions} + \text{Approved Medical Sessions}}{\text{Total Sessions Held (15)}} \right) \times 100$$
* Provides dynamic filtering and percentage computations for:
  * **Theory-Only Attendance %**
  * **Practical-Only Attendance %**
  * **Combined Attendance %** (over 30 total sessions)
* Evaluates eligibility status: `ELIGIBLE (>= 80%)` vs `SHORTAGE / NOT ELIGIBLE (< 80%)`.
* Includes one-click **CSV Report Export** for batch audit sheets.

### 3. Medical Certificate Lifecycle & Absence Excusal
* Medical submissions support three review states: `PENDING`, `APPROVED`, and `REJECTED`.
* Inspects medical grounds (e.g., hospital admission, doctor certifications, illness details).
* **Automatic Absence Excusal:** When approved by the Technical Officer, the session is counted as an excused session in the attendance percentage calculation, directly impacting exam eligibility.
* Provides real-time KPI metrics: Total Submissions, Pending Review, Approved (Excused), and Rejected.

### 4. Department Timetable & Laboratory Allocations
* Displays weekly departmental lecture and computing lab slots (Computer Lab 1, Computer Lab 2, Hardware Lab, Bioprocess Lab, Engineering Workshop).
* Filterable by Department (`ICT`, `BST`, `ET`), Day of Week (`Monday`–`Friday`), and Session Type (`PRACTICAL / LAB` vs `THEORY`).
* Supports CSV export for departmental lab schedules.

### 5. Official Faculty Notices Viewer
* Allows Technical Officers to view faculty notices, exam eligibility circulars, timetable memos, and medical submission deadlines.
* Features real-time keyword search, Category filtering (`ACADEMIC`, `EXAMINATION`, `TIMETABLE`, `GENERAL`), and Priority highlighting (`URGENT`, `NORMAL`, `INFO`).

### 6. Profile Management
* Technical Officers can view and update their personal details (First Name, Last Name, Email, Contact Number) while username and role credentials remain securely locked.

---

## 3. Demonstration of Mandatory OOP Concepts (For Report & Viva)

| OOP Concept | Where It Is Used in Member 3 Code | Technical Justification |
| :--- | :--- | :--- |
| **Classes & Objects** | `TechnicalOfficer`, `Attendance`, `AttendanceSession`, `Medical`, `StudentAttendanceSummary` | Real-world academic entities are modeled as discrete classes with state and behavior. Objects are dynamically instantiated across controllers and views. |
| **Inheritance** | `TechnicalOfficer extends User` | `TechnicalOfficer` inherits all foundational user identity attributes (userId, username, password, email, status) from the base `User` class, eliminating redundant code. |
| **Abstraction** | `User.java` (abstract base), `AttendanceDAO`, `MedicalDAO` | The abstract `User` class declares abstract methods `getRoleTitle()` and `getDashboardGreeting()`. The DAO pattern abstracts complex SQL queries (`JOIN`, `GROUP BY`, `COUNT`) behind clean Java methods (`findByStudent()`, `getBatchAttendanceSummary()`). |
| **Polymorphism** | Dynamic dispatch on `currentUser.getRoleTitle()`, `currentUser.getDashboardGreeting()` | The login controller and headers treat all users uniformly as `User` references, but at runtime dynamic method dispatch executes the overridden `TechnicalOfficer` implementations. Also demonstrated via method overloading in DAOs. |
| **Encapsulation** | All models (`TechnicalOfficer`, `Medical`, `AttendanceSession`) | All internal fields are declared `private`. Access is guarded via public getters and setters. State validation is enforced (e.g., date formatting, status enumerations, attendance threshold bounds). |
| **Exception Handling** | Custom and built-in exceptions (`DatabaseException`, `ValidationException`, `DateTimeParseException`, `SQLException`) | Guarded against invalid date inputs, database offline states, and data constraint violations using `try-catch-finally` blocks and custom exception hierarchies. |
| **Database Handling** | Pure JDBC in `AttendanceDAO.java` and `MedicalDAO.java` | Uses parameterized `PreparedStatement` to prevent SQL injection, database transaction batching, foreign key constraints (`ON DELETE CASCADE`), and automatic table migration fallbacks. |

---

## 4. Database Seed Data Scenarios Covered (`06_insert_attendance_medical.sql`)

The seed script covers all 5 mandatory evaluation scenarios across 20 undergraduate students (`user_id` 11 to 30):

1. **Scenario 1 ($> 80\%$ Attendance):**  
   Students 11–14 (`tg2021001`–`tg2021004`): Attended 14 or 15 out of 15 sessions ($93.3\% - 100\%$). Result: `ELIGIBLE`.
2. **Scenario 2 (Exactly $80\%$ Attendance):**  
   Students 15–17 (`tg2021005`–`tg2021007`): Attended exactly 12 out of 15 sessions ($80.0\%$). Result: `ELIGIBLE`.
3. **Scenario 3 ($< 80\%$ Attendance without Medicals):**  
   Students 18–21 (`tg2021008`–`tg2021011`): Attended 9 out of 15 sessions ($60.0\%$), zero medical submissions. Result: `NOT ELIGIBLE (SHORTAGE)`.
4. **Scenario 4 ($> 80\%$ Attendance with Medicals):**  
   Students 22–24 (`tg2021012`–`tg2021014`): Attended 13 out of 15 sessions ($86.7\%$) plus approved medical certificates on record for excused days. Result: `ELIGIBLE`.
5. **Scenario 5 ($< 80\%$ Attendance with Approved Medicals):**  
   Students 25–27 (`tg2021015`–`tg2021017`): Attended only 10 out of 15 sessions ($66.7\%$), but submitted 3 approved medical certificates. Effective attendance becomes:
   $$\frac{10 + 3}{15} \times 100 = 86.7\% \ge 80.0\% \implies \text{ELIGIBLE}$$
6. **Additional Scenarios:**  
   Student 28 has `PENDING` medicals awaiting review; Students 29 and 30 represent repeat students with `REJECTED` medicals ($46.7\%$, ineligible).

---

## 5. Individual Contribution Report (1-Page Submission Text)

*(Copy the section below directly into your group's final LMS report under "Individual Contribution — Member 3: Hansika Munaweera")*

```text
Individual Contribution Report
Student Name: Hansika Munaweera
Assigned Role: Member 3 — Technical Officer, Attendance & Medical Records Module
Course Unit: ICT2132 – Object Oriented Programming Practicum

1. Scope of Work & Responsibilities:
As Member 3 of the Faculty Management System project, I was responsible for the end-to-end design, implementation, and database integration of the Technical Officer module. My primary responsibilities encompassed daily undergraduate attendance tracking, medical certificate verification, departmental timetable scheduling, and faculty notice dissemination.

2. Architecture & Implementation:
I developed the module adhering strictly to Java Standard Edition (Java SE 17+) and pure Java Swing/AWT without external UI frameworks, following the project's layered architectural pattern (Model-DAO-GUI-Exception):
- Models: Developed TechnicalOfficer (subclassing User), Attendance, AttendanceSession, Medical, and StudentAttendanceSummary.
- Data Access: Implemented AttendanceDAO and MedicalDAO utilizing JDBC PreparedStatements for safe, parameterized database interactions.
- GUI Workspaces: Built the TechnicalOfficerDashboard (responsive 3x2 modern card layout), AttendanceEntryDialog (supporting sessions 1 to 15, date validation, and batch marking), ViewAttendanceDialog (Theory/Practical/Combined calculations, 80% UGC threshold filtering, and CSV export), MedicalManagementDialog (medical certificate inspection, approval/rejection workflows, and KPI metrics), DepartmentTimetableDialog (lab and lecture schedule viewer by department), NoticeViewerDialog (official circulars browser), and TechnicalOfficerProfileDialog.
- Database: Authored the relational schema for attendance_sessions, attendance_records, and medical_records in database/02_create_tables.sql and developed database/06_insert_attendance_medical.sql to pre-populate realistic attendance data covering all 5 mandatory UGC evaluation scenarios.

3. Demonstration of Object-Oriented Programming (OOP) Principles:
- Classes and Objects: Modeled core university operations through strongly-typed entity classes and instantiated domain objects across data and presentation layers.
- Inheritance: Extended the abstract base User class into TechnicalOfficer, reusing core user attributes and lifecycle behaviors.
- Abstraction: Abstracted persistence logic through the DAO pattern and implemented abstract polymorphic methods (getRoleTitle(), getDashboardGreeting()).
- Polymorphism: Overrode parent methods to achieve dynamic runtime role dispatch and applied method overloading in DAO query handlers.
- Encapsulation: Enforced information hiding with private fields, validated accessors/mutators, and input sanitation.
- Exception Handling: Handled runtime failures gracefully using custom exceptions (DatabaseException, ValidationException) and UI error dialogs.
- Database Handling: Connected to MySQL via JDBC, managing relational integrity with foreign keys, transactional batch operations, and aggregate queries.

4. Deliverables & Version Control:
All source code, database scripts, and test records were developed, verified, and committed on the git branch 'hansika', successfully merged into the team 'test' branch, and validated with zero compilation errors.
```

---

## 6. Viva Voce & Code Defense Guide (Expected Questions & Answers)

### Q1: How does your module calculate the 80% examination eligibility when a student submits medicals?
**Answer:**  
In `AttendanceDAO.java` and `ViewAttendanceDialog.java`, the system counts attended sessions where `status = 'PRESENT'` and adds sessions with `status = 'APPROVED'` from `medical_records`. The combined score is divided by the total sessions held (15 for Theory, 15 for Practical, or 30 Combined) and multiplied by 100. If the resulting percentage is $\ge 80.0\%$, the student is flagged as `ELIGIBLE`; otherwise, `SHORTAGE / NOT ELIGIBLE`.

### Q2: How does your code demonstrate Inheritance and Polymorphism?
**Answer:**  
`TechnicalOfficer` extends the abstract base class `User`. It inherits fields like `userId`, `username`, `email`, and `contactNo`. Polymorphism is demonstrated when `TechnicalOfficer` overrides `getRoleTitle()` (returns `"Technical Officer"`) and `getDashboardGreeting()` (returns a personalized staff greeting). When the `LoginController` authenticates the user, it stores a `User` reference, but dynamically invokes the `TechnicalOfficer` overridden methods at runtime.

### Q3: Where is Encapsulation applied in your code?
**Answer:**  
All fields in `TechnicalOfficer`, `AttendanceSession`, and `Medical` are declared `private`. They can only be accessed or modified through getters and setters. In `TechnicalOfficerProfileDialog`, encapsulation prevents students or unauthorized roles from altering immutable fields like `username` and `role_id`, while permitting safe updates to contact details.

### Q4: How did you prevent SQL Injection and handle database errors?
**Answer:**  
In `AttendanceDAO` and `MedicalDAO`, all database queries strictly use parameterized `PreparedStatement` objects with placeholders (`?`) rather than concatenated SQL strings. Any `SQLException` caught during execution is wrapped inside a custom `DatabaseException` and logged, while presenting user-friendly warning dialogs without crashing the desktop application. Additionally, an offline in-memory fallback dataset is provided if the database is unreachable.

### Q5: What happens when a Technical Officer rejects a medical submission?
**Answer:**  
In `MedicalManagementDialog.java`, when the Technical Officer clicks "Reject", `MedicalDAO.updateMedicalStatus()` updates the status in `medical_records` to `'REJECTED'`. Because only `'APPROVED'` medicals are credited towards attendance, rejected medical sessions remain counted as unexcused absences, leaving the student's attendance percentage unaffected.
