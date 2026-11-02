package turfease;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class TimeSlot {

    private static int idCounter = 1;

    private final String slotId;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private boolean booked;

    public TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        this.slotId = "SLOT" + (idCounter++);
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.booked = false;
    }

    public String getSlotId() {
        return slotId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isBooked() {
        return booked;
    }

    void markBooked() {
        this.booked = true;
    }

    void markAvailable() {
        this.booked = false;
    }

    public double getDurationInHours() {
        Duration d = Duration.between(startTime, endTime);
        return d.toMinutes() / 60.0;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s - %s (%s)",
                slotId, date, startTime, endTime, booked ? "BOOKED" : "AVAILABLE");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return slotId.equals(timeSlot.slotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId);
    }
}