package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a nurse in the hospital system, extending the Person class and implementing the Schedulable interface.
 */
public class Nurse extends Person implements Schedulable {
    private final String nurseID;
    private List<LocalDate> availableDates;

    /**
     * Constructs a Nurse with the specified name, age, nurse ID, and available dates.
     *
     * @param name           the nurse's name
     * @param age            the nurse's age
     * @param nurseID        the nurse's unique ID
     * @param availableDates the list of available dates
     */
    public Nurse(String name, int age, String nurseID, List<LocalDate> availableDates) {
        super(name, age);
        this.nurseID = nurseID;
        this.availableDates = new ArrayList<>(availableDates);
    }

    public String getNurseID() {
        return nurseID;
    }

    @Override
    public List<LocalDate> getAvailableDates() {
        return new ArrayList<>(availableDates);
    }

    @Override
    public void setAvailableDates(List<LocalDate> dates) {
        this.availableDates = new ArrayList<>(dates);
    }

    @Override
    public boolean isAvailableOn(LocalDate date) {
        return availableDates.contains(date);
    }

    @Override
    public void displayInfo() {
        System.out.println("Nurse ID: " + nurseID + ", Name: " + getName() + ", Age: " + getAge() +
                ", Available Dates: " + availableDates);
    }

    @Override
    public String toString() {
        return "Nurse{ID='" + nurseID + "', name='" + getName() + "', age=" + getAge() +
                ", availableDates=" + availableDates + "}";
    }
}