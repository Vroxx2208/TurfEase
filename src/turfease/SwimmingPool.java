package turfease;

public class SwimmingPool extends SportsFacility {

    private static final double LIFEGUARD_FEE = 100.0;
    private static final double MAX_HOURS_PER_SLOT = 2.0;

    public SwimmingPool(String name, String location, double baseRatePerHour) {
        super(name, location, baseRatePerHour);
    }

    @Override
    public double calculatePrice(TimeSlot slot) {
        double hours = Math.min(slot.getDurationInHours(), MAX_HOURS_PER_SLOT);
        return getBaseRatePerHour() * hours + LIFEGUARD_FEE;
    }

    @Override
    public FacilityType getType() {
        return FacilityType.SWIMMING_POOL;
    }
}