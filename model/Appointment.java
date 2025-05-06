package model;

import java.time.LocalDate;

public class Appointment implements Schedulable {
    private String appointmentID;
    private Doctor doctor;
    private Patient patient;
    private LocalDate appointmentDate;
    private static int appointmentCounter = 513;

    @Override
    public void scheduleAppointment(Doctor doctor, Patient patient, LocalDate date) {
        if (doctor.isAvailable(date)) {
            this.doctor = doctor;
            this.patient = patient;
            this.appointmentDate = date;
            this.appointmentID = "APT" + (appointmentCounter++);
        } else {
            throw new IllegalArgumentException("Doctor is not available on " + date);
        }
    }

    // Getters
    public String getAppointmentID() { return appointmentID; }
    public Doctor getDoctor() { return doctor; }
    public Patient getPatient() { return patient; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
}