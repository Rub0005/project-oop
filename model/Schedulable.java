package model;

import java.time.LocalDate;

public interface Schedulable {
    void scheduleAppointment(Doctor doctor, Patient patient, LocalDate date);
}