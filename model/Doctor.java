package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Doctor extends Person {
    private String doctorID;
    private String specialization;
    private List<LocalDate> availableDates;

    public Doctor(String name, int age, String doctorID, String specialization, List<LocalDate> availableDates) {
        super(name, age);
        this.doctorID = doctorID;
        this.specialization = specialization;
        this.availableDates = new ArrayList<>(availableDates);
    }

    // Getters and setters
    public String getDoctorID() { return doctorID; }
    public String getSpecialization() { return specialization; }
    public List<LocalDate> getAvailableDates() { return availableDates; }

    public void setDoctorID(String doctorID) { this.doctorID = doctorID; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setAvailableDates(List<LocalDate> availableDates) {
        this.availableDates = new ArrayList<>(availableDates);
    }

    public boolean isAvailable(LocalDate date) {
        return availableDates.contains(date);
    }

    @Override
    public void displayInfo() {
        System.out.println("Doctor name: " + getName() + " Age: " + getAge());
        System.out.println("Doctor ID: " + doctorID);
        System.out.println("Specialization: " + specialization);
        System.out.println("Available Dates: " + availableDates);
    }
}