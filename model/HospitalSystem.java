package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HospitalSystem {
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Appointment> appointments;
    private String adminPassword = "admin123";

    public HospitalSystem() {
        doctors = new ArrayList<>();
        patients = new ArrayList<>();
        appointments = new ArrayList<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        List<LocalDate> dr1Dates = new ArrayList<>();
        dr1Dates.add(LocalDate.now().plusDays(1));
        dr1Dates.add(LocalDate.now().plusDays(2));

        List<LocalDate> dr2Dates = new ArrayList<>();
        dr2Dates.add(LocalDate.now().plusDays(3));
        dr2Dates.add(LocalDate.now().plusDays(4));

        doctors.add(new Doctor("Dr. Smith", 45, "SM123", "Cardiologist", dr1Dates));
        doctors.add(new Doctor("Dr. Johnson", 38, "JO456", "Dermatologist", dr2Dates));
    }

    // Doctor methods
    public boolean addDoctor(Doctor doctor) {
        if (getDoctorById(doctor.getDoctorID()) != null) {
            return false;
        }
        doctors.add(doctor);
        return true;
    }

    public boolean removeDoctor(String doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor != null) {
            return doctors.remove(doctor);
        }
        return false;
    }

    public boolean updateDoctor(String doctorId, Doctor updatedDoctor) {
        Doctor existing = getDoctorById(doctorId);
        if (existing != null) {
            existing.setName(updatedDoctor.getName());
            existing.setAge(updatedDoctor.getAge());
            existing.setSpecialization(updatedDoctor.getSpecialization());
            existing.setAvailableDates(updatedDoctor.getAvailableDates());
            return true;
        }
        return false;
    }

    // Patient methods
    public String addPatient(Patient patient) {
        patients.add(patient);
        return patient.getPatientID();
    }

    public boolean removePatient(String patientId) {
        Patient patient = getPatientById(patientId);
        if (patient != null) {
            return patients.remove(patient);
        }
        return false;
    }

    public boolean updatePatient(String patientId, Patient updatedPatient) {
        Patient existing = getPatientById(patientId);
        if (existing != null) {
            existing.setName(updatedPatient.getName());
            existing.setAge(updatedPatient.getAge());
            existing.setDiseaseDescription(updatedPatient.getDiseaseDescription());
            return true;
        }
        return false;
    }

    // Appointment methods
    public boolean scheduleAppointment(String doctorId, String patientId, LocalDate date) {
        Doctor doctor = getDoctorById(doctorId);
        Patient patient = getPatientById(patientId);

        if (doctor == null || patient == null || !doctor.isAvailable(date)) {
            return false;
        }

        Appointment appointment = new Appointment();
        appointment.scheduleAppointment(doctor, patient, date);
        appointments.add(appointment);
        return true;
    }

    // Authentication
    public boolean verifyAdminPassword(String password) {
        return adminPassword.equals(password);
    }

    // Helper methods
    private Doctor getDoctorById(String id) {
        return doctors.stream()
                .filter(d -> d.getDoctorID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    private Patient getPatientById(String id) {
        return patients.stream()
                .filter(p -> p.getPatientID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    // Getters
    public List<Doctor> getAllDoctors() {
        return Collections.unmodifiableList(doctors);
    }

    public List<Patient> getAllPatients() {
        return Collections.unmodifiableList(patients);
    }

    public List<Appointment> getAllAppointments() {
        return Collections.unmodifiableList(appointments);
    }
}