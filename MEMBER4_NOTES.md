# Member 4 — Undergraduate, Timetable & Results
### ICT2132 Mini Project — implementation notes

This document covers what was built for Member 4's part of the group project,
the assumptions made (because Members 1–3's modules weren't finished yet),
and where to look for each OOP concept for the report / demo.

## 1. What's included

| Layer | Files |
|---|---|
| Model | `Course`, `Enrollment`, `Mark`, `TimetableEntry`, `Grade`, `Result` |
| DAO | `CourseDAO`, `EnrollmentDAO`, `TimetableDAO`, `GradeDAO`, `ResultDAO`, `UndergraduateDAO` |
| Service | `UndergraduateService`, `TimetableService`, `GradeService`, `ResultService`, `GPAService` |
| Util | `GradeCalculator` (marks → grade → grade point), `GPACalculator` (SGPA/CGPA) |
| Controller | `UndergraduateController` |
| GUI | `UndergraduateDashboard` (tabbed shell), `StudentProfilePanel`, `StudentCoursePanel`, `TimetablePanel`, `GradePanel`, `ResultPanel`, `SGPA_Panel`, `CGPA_Panel`, `PendingModulePanel`, `SemesterSelector` |
| Database | `database/M4_01_create_tables.sql`, `database/M4_02_seed_data.sql` |

Run the two SQL files after `01_create_database.sql`–`04_insert_users.sql`
(they reference `users`, which must already exist). They're both
`IF NOT EXISTS` / `ON DUPLICATE KEY UPDATE`, so re-running them is safe.

Demo login: `tg2021001` / `std123` (or any of `tg2021001`–`tg2021020`).

## 2. Why some tables/models here aren't "pure" Member 4

`courses` and `marks` are logically owned by Member 1 and Member 2, but
neither existed yet, so minimal versions were created here (`IF NOT EXISTS`)
so this module could be built and demoed independently. If Member 1/2 build
richer versions, **merge the column sets — don't just drop these files.**
Everything Member 4 owns (`enrollments`, `timetable`, `grades`, `results`)
only needs `course_id` and the marks columns to exist somewhere; it doesn't
care who created them.

## 3. Assumptions made (flag these to your course coordinators / group)

1. **Grading scale** — the actual UGC Commission Circular No. 12-2024 PDF is
   a scanned, non-text document, so it couldn't be read directly. The scale
   used in `GradeCalculator.java` is the standard Sri Lankan UGC grading
   scale used across the university system (A+ down to E). **Double-check
   this against the real circular before final submission** — if it differs,
   only the `GRADE_BANDS` array in `GradeCalculator` needs to change.
2. **CA vs. Final Exam weighting** — the brief says all component marks
   (quiz/assignment/mid-sem/practical/final) are out of 100 but doesn't give
   an exact weighting formula. This implementation uses:
   - CA% = average of whichever CA components were entered
   - Final total = 40% CA + 60% Final Exam
   Change `CA_WEIGHT` / `FINAL_EXAM_WEIGHT` in `GradeCalculator` if your
   course coordinators specify different weights.
3. **CA eligibility only** — "Eligibility" in the brief is CA ≥ 40% *and*
   attendance ≥ 80% (Member 3's data). Since attendance isn't built yet,
   Grade/Result computation here only checks the CA ≥ 40% rule. Once
   Member 3's `attendance` table exists, add an attendance check into
   `GradeDAO.computeAndSaveGrade()` before marking a grade eligible.
4. **Repeat / batch-missed students** — two seeded students (`tg2021019`,
   `tg2021020`) have deliberately low CA marks in one course each, to
   demonstrate the ineligible/repeat scenario the assignment brief asks for.

## 4. OOP concepts demonstrated (for the report)

- **Classes & Objects** — `Undergraduate`, `Course`, `Enrollment`, `Mark`, `Grade`, `Result`, `TimetableEntry`
- **Inheritance** — `Undergraduate extends User`
- **Abstraction** — `GradeCalculator` / `GPACalculator` hide the banding &
  weighted-average logic behind one method each (`marksToGradeLetter()`,
  `computeSgpa()`/`computeCgpa()` share one private helper)
- **Polymorphism** — `Undergraduate` overrides `getRoleTitle()` and
  `getDashboardGreeting()` from the abstract `User` base class
- **Encapsulation** — all models are private fields + getters/setters;
  `UndergraduateDAO` deliberately exposes no generic "update everything"
  method, only `updateContactAndPicture()`, enforcing the "students can't
  edit username/password/academic data" rule at the data layer, not just the GUI
- **Exception Handling** — `BusinessRuleException`, `ValidationException`,
  `DatabaseException` used throughout the service/controller layers
- **Database Handling** — JDBC CRUD across all 6 DAOs, `ON DUPLICATE KEY
  UPDATE` upserts for computed Grades/Results
- **GUI** — Swing, `UndergraduateDashboard` + 8 tabbed panels

## 5. Known gaps / next steps for the group

- Attendance & Medical tabs are placeholders pending Member 3's module.
- Once Member 2 builds a real `MarkDAO`/mark-entry screen, marks will come
  from the Lecturer's uploads instead of the seed script — no Member 4 code
  needs to change, since `GradeDAO` just reads whatever's in the `marks` table.
- Once Member 1 builds Timetable creation (Admin side), the seeded timetable
  rows can be replaced/extended — `TimetableDAO`'s read queries don't care
  who wrote the rows.
