package turfease;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingManager {

    private final Map<String, SportsFacility> facilities = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();

    // ---------- Admin operations ----------

    public void addFacility(SportsFacility facility) {
        facilities.put(facility.getFacilityId(), facility);
    }

    public void addTimeSlot(String facilityId, LocalDate date, LocalTime start, LocalTime end) {
        SportsFacility facility = facilities.get(facilityId);
        if (facility == null) {
            System.out.println("No facility found with id " + facilityId);
            return;
        }
        facility.addTimeSlot(new TimeSlot(date, start, end));
    }

    public void updateFacilityPrice(String facilityId, double newBaseRate) {
        SportsFacility facility = facilities.get(facilityId);
        if (facility == null) {
            System.out.println("No facility found with id " + facilityId);
            return;
        }
        facility.setBaseRatePerHour(newBaseRate);
    }

    // ---------- Browsing ----------

    public List<SportsFacility> listAllFacilities() {
        return new ArrayList<>(facilities.values());
    }

    public List<SportsFacility> listFacilitiesByType(FacilityType type) {
        List<SportsFacility> result = new ArrayList<>();
        for (SportsFacility f : facilities.values()) {
            if (f.getType() == type) {
                result.add(f);
            }
        }
        return result;
    }

    public SportsFacility findFacilityById(String facilityId) {
        return facilities.get(facilityId);
    }

    // ---------- Availability + booking ----------

    public boolean checkAvailability(String facilityId, String slotId) {
        SportsFacility facility = facilities.get(facilityId);
        if (facility == null) {
            return false;
        }
        TimeSlot slot = facility.findSlotById(slotId);
        return slot != null && !slot.isBooked();
    }

    public Booking bookFacility(User user, String facilityId, String slotId) {
        SportsFacility facility = facilities.get(facilityId);
        if (facility == null) {
            System.out.println("No facility found with id " + facilityId);
            return null;
        }

        TimeSlot slot = facility.findSlotById(slotId);
        if (slot == null) {
            System.out.println("No such time slot for this facility.");
            return null;
        }

        if (slot.isBooked()) {
            System.out.println("Sorry, that slot is already booked. Try another one.");
            return null;
        }

        double price = facility.calculatePrice(slot);
        Booking booking = new Booking(user, facility, slot, price);

        slot.markBooked();
        bookings.put(booking.getBookingId(), booking);
        user.addBooking(booking);

        System.out.println("Booking confirmed: " + booking.getBookingId()
                + " | Price: " + price);
        return booking;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("No booking found with id " + bookingId);
            return false;
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("Booking " + bookingId + " is already cancelled.");
            return false;
        }

        booking.cancel();
        booking.getTimeSlot().markAvailable();
        System.out.println("Booking " + bookingId + " cancelled.");
        return true;
    }

    // Permanently removes a booking. If it was still active, its slot is freed.
    public boolean deleteBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            System.out.println("No booking found with id " + bookingId);
            return false;
        }

        if (booking.getStatus() != BookingStatus.CANCELLED) {
            booking.cancel();
            booking.getTimeSlot().markAvailable();
        }

        bookings.remove(bookingId);
        booking.getUser().removeBooking(booking);
        System.out.println("Booking " + bookingId + " deleted.");
        return true;
    }

    // Rebuilds a booking that was loaded from the saved file
    Booking restoreBooking(User user, SportsFacility facility, TimeSlot slot,
                           String bookingId, double price,
                           LocalDateTime bookedAt, BookingStatus status) {
        Booking booking = new Booking(bookingId, user, facility, slot, price, bookedAt, status);
        if (status != BookingStatus.CANCELLED) {
            slot.markBooked();
        }
        bookings.put(bookingId, booking);
        user.addBooking(booking);
        return booking;
    }

    public List<Booking> viewAllBookings() {
        return new ArrayList<>(bookings.values());
    }
}