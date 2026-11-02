package turfease;

import java.time.LocalDate;
import java.time.LocalTime;

public class Admin extends Person {

    public Admin(String name, String email, String phone) {
        super(name, email, phone);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public void addFacility(BookingManager manager, SportsFacility facility) {
        manager.addFacility(facility);
        System.out.println(getName() + " added facility: " + facility.getName());
    }

    public void addTimeSlot(BookingManager manager, String facilityId, LocalDate date,
                             LocalTime start, LocalTime end) {
        manager.addTimeSlot(facilityId, date, start, end);
        System.out.println(getName() + " added a time slot for facility " + facilityId);
    }

    public void updatePrice(BookingManager manager, String facilityId, double newBaseRate) {
        manager.updateFacilityPrice(facilityId, newBaseRate);
        System.out.println(getName() + " updated price for facility " + facilityId
                + " to " + newBaseRate + "/hr");
    }

    public void cancelAnyBooking(BookingManager manager, String bookingId) {
        manager.cancelBooking(bookingId);
        System.out.println(getName() + " (admin) cancelled booking " + bookingId);
    }
}