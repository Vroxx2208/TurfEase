package turfease;

public class CricketTurf extends SportsFacility {

    private static final double EQUIPMENT_FEE = 150.0;

    public CricketTurf(String name, String location, double baseRatePerHour) {
        super(name, location, baseRatePerHour);
    }

    @Override
    public double calculatePrice(TimeSlot slot) {
        return getBaseRatePerHour() * slot.getDurationInHours() + EQUIPMENT_FEE;
    }

    @Override
    public FacilityType getType() {
        return FacilityType.CRICKET_TURF;
    }
}
