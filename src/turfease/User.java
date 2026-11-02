package turfease;

import java.util.ArrayList;
import java.util.List;

public class User extends Person {

    private final List<Booking> bookingHistory = new ArrayList<>();

    public User(String name, String email, String phone) {
        super(name, email, phone);
    }

    @Override
    public String getRole() {
        return "USER";
    }

    void addBooking(Booking booking) {
        bookingHistory.add(booking);
    }

    void removeBooking(Booking booking) {
        bookingHistory.remove(booking);
    }

    public List<Booking> getBookingHistory() {
        return new ArrayList<>(bookingHistory);
    }

    public void printBookingHistory() {
        System.out.println("---- Booking history for " + getName() + " ----");
        if (bookingHistory.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        for (Booking b : bookingHistory) {
            System.out.println(b);
        }
    }
}