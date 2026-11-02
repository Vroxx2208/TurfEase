package turfease;

import java.time.LocalTime;

public class FootballTurf extends SportsFacility {

    private static final double PEAK_SURCHARGE_PERCENT = 20.0;
    private static final LocalTime PEAK_START = LocalTime.of(17, 0);

    public FootballTurf(String name, String location, double baseRatePerHour) {
        super(name, location, baseRatePerHour);
    }

    @Override
    public double calculatePrice(TimeSlot slot) {
        double price = getBaseRatePerHour() * slot.getDurationInHours();
        if (!slot.getStartTime().isBefore(PEAK_START)) {
            price += price * (PEAK_SURCHARGE_PERCENT / 100.0);
        }
        return price;
    }

    @Override
    public FacilityType getType() {
        return FacilityType.FOOTBALL_TURF;
    }
}