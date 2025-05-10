Hospital Management System

Overview
The Hospital Management System is a Java-based application designed to streamline the management of hospital staff, patients, and appointments. This system provides a graphical user interface (GUI) built with Swing, allowing administrators to manage doctors and nurses, register patients, and schedule appointments. 

Staff Management: Add, update, and delete doctors and nurses with details such as name, age, specializations (for doctors), and available dates.

Patient Management: Register, update, and remove patients with details including name, age, and disease description.

Appointment Scheduling: Schedule, update, and cancel appointments between staff and patients, with rules enforcement:
Patients requiring bandaging (e.g., "Leg injury") can only be scheduled with nurses.
Patients under 18 can only be scheduled with pediatricians.


Authentication: Supports admin and guest modes, with admin authentication to restrict access to sensitive operations.
Search Functionality: Filter staff, patients, and appointments by name or ID for quick lookup.

Usage

Launch the Application: Start the program, which will prompt for admin authentication.
Admin Mode: Log in with username "admin" and password "admin123" to access full functionality (staff, patient, and appointment management).
Guest Mode: Decline authentication to view staff details in read-only mode.
Navigation: Use the tabbed interface to switch between Staff, Patients, and Appointments sections.
Operations: Click buttons to add, update, delete, or schedule, and use the search fields to filter records.

Rules and Constraints

Bandaging Rule: Patients with diseases containing "wound" or "bandage" are restricted to nurses.
Pediatric Rule: Patients under 18 are restricted to doctors with the "Pediatrics" specialization.
ID Generation: Doctors use a sequential "DOC-XXX" format, patients use a name-based "XX-XXX" format, and nurses use a manual "NUR-XXX" format.


By Rubik Stamboltsyan, Emil Azatyan
