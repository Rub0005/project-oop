package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a doctor in the hospital system with their details and availability.
 */
public class Doctor implements Schedulable {
    private static int doctorCounter = 0; // Static counter for generating unique IDs
    private final String doctorID;
    private String name;
    private int age;
    private List<String> specializations;
    private List<LocalDate> availableDates;

    public Doctor(String name, int age, List<String> specializations, List<LocalDate> availableDates) {
        this.doctorID = generateDoctorID();
        this.name = name;
        this.age = age;
        this.specializations = specializations != null ? List.copyOf(specializations) : List.of();
        this.availableDates = availableDates != null ? List.copyOf(availableDates) : List.of();
    }

    private String generateDoctorID() {
        return "DOC-" + String.format("%03d", ++doctorCounter);
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<String> getSpecializations() {
        return specializations;
    }

    public List<LocalDate> getAvailableDates() {
        return availableDates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations != null ? List.copyOf(specializations) : List.of();
    }

    public void setAvailableDates(List<LocalDate> availableDates) {
        this.availableDates = availableDates != null ? List.copyOf(availableDates) : List.of();
    }

    @Override
    public boolean isAvailableOn(LocalDate date) {
        return availableDates.contains(date);
    }

    public boolean hasSpecialization(String specialization) {
        return specializations.stream().anyMatch(s -> s.equalsIgnoreCase(specialization));
    }

    @Override
    public String toString() {
        return "Doctor{" + "doctorID='" + doctorID + '\'' + ", name='" + name + '\'' + ", age=" + age +
                ", specializations=" + specializations + ", availableDates=" + availableDates + '}';
    }
}