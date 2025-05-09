package model;

public class Patient extends Person {
    private final String patientID;
    private String diseaseDescription;

    /**
     * Constructs a Patient with the specified ID, name, age, and disease description.
     *
     * @param patientID          the unique patient ID
     * @param name               the patient's name
     * @param age                the patient's age
     * @param diseaseDescription the description of the patient's disease
     */
    public Patient(String patientID, String name, int age, String diseaseDescription) {
        super(name, age);
        this.patientID = patientID;
        this.diseaseDescription = diseaseDescription;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    /**
     * Determines if the patient requires bandaging based on their disease description.
     *
     * @return true if the disease description contains "injury" or "wound", false otherwise
     */
    public boolean requiresBandaging() {
        return diseaseDescription.toLowerCase().contains("wound") || diseaseDescription.toLowerCase().contains("bandage");
    }

    @Override
    public void displayInfo() {
        System.out.println("Patient ID: " + patientID + ", Name: " + getName() + ", Age: " + getAge() +
                ", Disease: " + diseaseDescription);
    }

    @Override
    public String toString() {
        return "Patient{ID='" + patientID + "', name='" + getName() + "', age=" + getAge() +
                ", diseaseDescription='" + diseaseDescription + "'}";
    }
}