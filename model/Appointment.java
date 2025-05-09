package model;

import java.time.LocalDate;

/**
 * Represents an appointment in the hospital system.
 */
public class Appointment {
    private final String appointmentID;
    private final String medicalStaffName;
    private final String medicalStaffType;
    private final Patient patient;
    private final LocalDate appointmentDate;

    /**
     * Constructs an Appointment with the specified details.
     *
     * @param appointmentID      the unique ID of the appointment
     * @param medicalStaffName   the name of the medical staff
     * @param medicalStaffType   the type of medical staff (Doctor or Nurse)
     * @param patient            the patient for the appointment
     * @param appointmentDate    the date of the appointment
     */
    public Appointment(String appointmentID, String medicalStaffName, String medicalStaffType,
                       Patient patient, LocalDate appointmentDate) {
        this.appointmentID = appointmentID;
        this.medicalStaffName = medicalStaffName;
        this.medicalStaffType = medicalStaffType;
        this.patient = patient;
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public String getMedicalStaffName() {
        return medicalStaffName;
    }

    public String getMedicalStaffType() {
        return medicalStaffType;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    @Override
    public String toString() {
        return "Appointment{ID='" + appointmentID + "', staff='" + medicalStaffName +
                "', type='" + medicalStaffType + "', patient='" + patient.getName() +
                "', date=" + appointmentDate + "}";
    }
}