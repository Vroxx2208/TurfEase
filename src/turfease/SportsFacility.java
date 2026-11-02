package turfease;

import java.util.ArrayList;
import java.util.List;

public abstract class SportsFacility {

    private static int idCounter = 1;

    private final String facilityId;
    private String name;
    private String location;
    private double baseRatePerHour;
    private final List<TimeSlot> timeSlots = new ArrayList<>();

    protected SportsFacility(String name, String location, double baseRatePerHour) {
        this.facilityId = "FAC" + (idCounter++);
        this.name = name;
        this.location = location;
        this.baseRatePerHour = baseRatePerHour;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getBaseRatePerHour() {
        return baseRatePerHour;
    }

    public void setBaseRatePerHour(double baseRatePerHour) {
        this.baseRatePerHour = baseRatePerHour;
    }

    public abstract double calculatePrice(TimeSlot slot);

    public abstract FacilityType getType();

    public void addTimeSlot(TimeSlot slot) {
        timeSlots.add(slot);
    }

    public List<TimeSlot> getTimeSlots() {
        return new ArrayList<>(timeSlots);
    }

    public List<TimeSlot> getAvailableTimeSlots() {
        List<TimeSlot> available = new ArrayList<>();
        for (TimeSlot slot : timeSlots) {
            if (!slot.isBooked()) {
                available.add(slot);
            }
        }
        return available;
    }

    public TimeSlot findSlotById(String slotId) {
        for (TimeSlot slot : timeSlots) {
            if (slot.getSlotId().equals(slotId)) {
                return slot;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s | %s (%s) at %s - base rate: %.2f/hr",
                facilityId, name, getType(), location, baseRatePerHour);
    }
}