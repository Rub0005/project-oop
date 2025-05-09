package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages the hospital system's data, including doctors, nurses, patients, appointments, and admin authentication.
 */
public class HospitalSystem {
    private static final Logger LOGGER = Logger.getLogger(HospitalSystem.class.getName());
    private final List<Doctor> doctors;
    private final List<Nurse> nurses;
    private final List<Patient> patients;
    private final List<Appointment> appointments;
    private final Map<String, String> adminCredentials; // username -> hashed password

    public HospitalSystem() {
        doctors = new ArrayList<>();
        nurses = new ArrayList<>();
        patients = new ArrayList<>();
        appointments = new ArrayList<>();
        adminCredentials = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Default doctors sample
        doctors.add(new Doctor("Aram Petrosyan", 45, List.of("Cardiology"), List.of(
                LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 12), LocalDate.of(2025, 5, 14))));
        doctors.add(new Doctor("Anahit Sarkisyan", 38, List.of("Pediatrics"), List.of(
                LocalDate.of(2025, 5, 11), LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 15))));
        doctors.add(new Doctor("Hayk Grigoryan", 50, List.of("Neurology"), List.of(
                LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 12), LocalDate.of(2025, 5, 16))));
        doctors.add(new Doctor("Lilit Harutyunyan", 42, List.of("Orthopedics"), List.of(
                LocalDate.of(2025, 5, 11), LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 14))));
        doctors.add(new Doctor("Tigran Mkrtchyan", 47, List.of("Oncology"), List.of(
                LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 15), LocalDate.of(2025, 5, 16))));
        doctors.add(new Doctor("Sona Avetisyan", 39, List.of("Dermatology"), List.of(
                LocalDate.of(2025, 5, 12), LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 14))));
        doctors.add(new Doctor("Vardan Baghdasaryan", 44, List.of("Surgery"), List.of(
                LocalDate.of(2025, 5, 11), LocalDate.of(2025, 5, 15), LocalDate.of(2025, 5, 16))));
        doctors.add(new Doctor("Narine Galstyan", 41, List.of("Endocrinology"), List.of(
                LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 15))));
        doctors.add(new Doctor("Gevorg Melkonyan", 49, List.of("Psychiatry"), List.of(
                LocalDate.of(2025, 5, 12), LocalDate.of(2025, 5, 14), LocalDate.of(2025, 5, 16))));
        doctors.add(new Doctor("Mariam Khachaturyan", 36, List.of("Ophthalmology"), List.of(
                LocalDate.of(2025, 5, 11), LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 15))));

        // Sample nurses
        nurses.add(new Nurse("Hasmik Hovhannisyan", 30, "NUR-001", List.of(
                LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 12), LocalDate.of(2025, 5, 14))));
        nurses.add(new Nurse("Ruzanna Terteryan", 34, "NUR-002", List.of(
                LocalDate.of(2025, 5, 11), LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 15))));
        nurses.add(new Nurse("Artak Karapetyan", 28, "NUR-003", List.of(
                LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 12), LocalDate.of(2025, 5, 16))));
        nurses.add(new Nurse("Zara Asatryan", 32, "NUR-004", List.of(
                LocalDate.of(2025, 5, 11), LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 14))));

        // Sample patients with generated IDs
        addPatient(new Patient(generatePatientID("Armen Vardanyan"), "Armen Vardanyan", 25, "Fever"));
        addPatient(new Patient(generatePatientID("Anna Mkrtchyan"), "Anna Mkrtchyan", 40, "Leg injury"));
        addPatient(new Patient(generatePatientID("Hovhannes Sahakyan"), "Hovhannes Sahakyan", 60, "Hypertension"));

        // Sample admin credentials (username: admin, password: admin123)
        try {
            String hashedPassword = hashPassword("admin123");
            adminCredentials.put("admin", hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe("Error initializing admin credentials: " + e.getMessage());
        }
    }

    public boolean addDoctor(Doctor doctor) {
        if (doctors.stream().anyMatch(d -> d.getDoctorID().equals(doctor.getDoctorID()))) {
            return false;
        }
        doctors.add(doctor);
        return true;
    }

    public boolean updateDoctor(String doctorID, Doctor updatedDoctor) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getDoctorID().equals(doctorID)) {
                doctors.set(i, updatedDoctor);
                return true;
            }
        }
        return false;
    }

    public boolean removeDoctor(String doctorID) {
        return doctors.removeIf(doctor -> doctor.getDoctorID().equals(doctorID));
    }

    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors);
    }

    public String addPatient(Patient patient) {
        String patientID = generatePatientID(patient.getName());
        Patient newPatient = new Patient(patientID, patient.getName(), patient.getAge(), patient.getDiseaseDescription());
        patients.add(newPatient);
        return patientID;
    }

    private String generatePatientID(String name) {
        String prefix;
        if (name == null || name.trim().isEmpty()) {
            prefix = "XX";
        } else {
            String cleanedName = name.trim().toUpperCase();
            if (cleanedName.length() < 2) {
                prefix = (cleanedName + "X").substring(0, 2);
            } else {
                prefix = cleanedName.substring(0, 2);
            }
        }

        for (int i = 1; i <= 999; i++) {
            String id = prefix + "-" + String.format("%03d", i);
            if (patients.stream().noneMatch(p -> p.getPatientID().equals(id))) {
                return id;
            }
        }
        throw new IllegalStateException("Cannot generate unique patient ID for prefix " + prefix + ": too many patients");
    }

    public boolean updatePatient(String patientID, Patient updatedPatient) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientID().equals(patientID)) {
                patients.set(i, updatedPatient);
                return true;
            }
        }
        return false;
    }

    public boolean removePatient(String patientID) {
        return patients.removeIf(patient -> patient.getPatientID().equals(patientID));
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public boolean scheduleAppointment(String staffID, String patientID, LocalDate date, boolean isNurse) {
        Schedulable staff = isNurse ? nurses.stream().filter(n -> n.getNurseID().equals(staffID)).findFirst().orElse(null)
                : doctors.stream().filter(d -> d.getDoctorID().equals(staffID)).findFirst().orElse(null);
        Patient patient = patients.stream().filter(p -> p.getPatientID().equals(patientID)).findFirst().orElse(null);

        if (staff == null || patient == null || !staff.isAvailableOn(date)) {
            return false;
        }

        // Enforce bandaging rule: Only nurses can handle patients requiring bandaging
        if (patient.requiresBandaging() && !isNurse) {
            return false;
        }

        // Enforce pediatric rule: Only pediatricians can handle patients under 18
        if (!isNurse && patient.getAge() < 18 && !((Doctor) staff).hasSpecialization("Pediatrics")) {
            return false;
        }

        String staffName = isNurse ? ((Nurse) staff).getName() : ((Doctor) staff).getName();
        String staffType = isNurse ? "Nurse" : "Doctor";
        appointments.add(new Appointment(UUID.randomUUID().toString(), staffName, staffType, patient, date));
        return true;
    }

    public boolean updateAppointment(String appointmentID, String staffID, String patientID, LocalDate date, boolean isNurse) {
        Appointment appointment = appointments.stream()
                .filter(a -> a.getAppointmentID().equals(appointmentID))
                .findFirst()
                .orElse(null);
        if (appointment == null) {
            return false;
        }

        Schedulable staff = isNurse ? nurses.stream().filter(n -> n.getNurseID().equals(staffID)).findFirst().orElse(null)
                : doctors.stream().filter(d -> d.getDoctorID().equals(staffID)).findFirst().orElse(null);
        Patient patient = patients.stream().filter(p -> p.getPatientID().equals(patientID)).findFirst().orElse(null);

        if (staff == null || patient == null || !staff.isAvailableOn(date)) {
            return false;
        }

        // Enforce bandaging rule: Only nurses can handle patients requiring bandaging
        if (patient.requiresBandaging() && !isNurse) {
            return false;
        }

        // Enforce pediatric rule: Only pediatricians can handle patients under 18
        if (!isNurse && patient.getAge() < 18 && !((Doctor) staff).hasSpecialization("Pediatrics")) {
            return false;
        }

        String staffName = isNurse ? ((Nurse) staff).getName() : ((Doctor) staff).getName();
        String staffType = isNurse ? "Nurse" : "Doctor";
        appointments.remove(appointment);
        appointments.add(new Appointment(appointmentID, staffName, staffType, patient, date));
        return true;
    }

    public boolean removeAppointment(String appointmentID) {
        return appointments.removeIf(appointment -> appointment.getAppointmentID().equals(appointmentID));
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    public List<Nurse> getAllNurses() {
        return new ArrayList<>(nurses);
    }

    public boolean verifyAdminPassword(String username, char[] password) {
        try {
            String hashedInputPassword = hashPassword(new String(password));
            String storedHashedPassword = adminCredentials.get(username);
            return storedHashedPassword != null && storedHashedPassword.equals(hashedInputPassword);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.severe("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}