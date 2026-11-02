package turfease;

public class BadmintonCourt extends SportsFacility {

    public BadmintonCourt(String name, String location, double baseRatePerHour) {
        super(name, location, baseRatePerHour);
    }

    @Override
    public double calculatePrice(TimeSlot slot) {
        return getBaseRatePerHour() * slot.getDurationInHours();
    }

    @Override
    public FacilityType getType() {
        return FacilityType.BADMINTON_COURT;
    }
}