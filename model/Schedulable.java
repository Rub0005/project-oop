package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for entities that can be scheduled in the hospital system.
 */
public interface Schedulable {
    List<LocalDate> getAvailableDates();
    void setAvailableDates(List<LocalDate> dates);
    boolean isAvailableOn(LocalDate date);
}