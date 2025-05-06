package model;

public class Patient extends Person {
    private String patientID;
    private String diseaseDescription;
    private static int patientCounter = 483;

    public Patient(String name, int age, String diseaseDescription) {
        super(name, age);
        this.patientID = name.substring(0, 2).toUpperCase() + (patientCounter++);
        this.diseaseDescription = diseaseDescription;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    public void setDiseaseDescription(String diseaseDescription) {
        this.diseaseDescription = diseaseDescription;
    }

    @Override
    public void displayInfo() {
        System.out.println("Patient Name: " + name + " age: " + age);
        System.out.println("Patient ID: " + patientID);
        System.out.println("Disease Description: " + diseaseDescription);
    }
}