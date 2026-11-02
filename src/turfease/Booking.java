package turfease;

import java.time.LocalDateTime;

public class Booking {

    private static int idCounter = 1;

    private final String bookingId;
    private final User user;
    private final SportsFacility facility;
    private final TimeSlot timeSlot;
    private final double price;
    private final LocalDateTime bookedAt;
    private BookingStatus status;

    public Booking(User user, SportsFacility facility, TimeSlot timeSlot, double price) {
        this.bookingId = "BKG" + (idCounter++);
        this.user = user;
        this.facility = facility;
        this.timeSlot = timeSlot;
        this.price = price;
        this.bookedAt = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    // Used when loading saved bookings from the file
    Booking(String bookingId, User user, SportsFacility facility, TimeSlot timeSlot,
            double price, LocalDateTime bookedAt, BookingStatus status) {
        this.bookingId = bookingId;
        this.user = user;
        this.facility = facility;
        this.timeSlot = timeSlot;
        this.price = price;
        this.bookedAt = bookedAt;
        this.status = status;

        // keep new booking ids from clashing with loaded ones
        try {
            int number = Integer.parseInt(bookingId.substring(3));
            if (number >= idCounter) {
                idCounter = number + 1;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            // ignore ids that don't follow the BKG<number> pattern
        }
    }

    public String getBookingId() {
        return bookingId;
    }

    public User getUser() {
        return user;
    }

    public SportsFacility getFacility() {
        return facility;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return String.format("%s | %s booked %s (%s) on %s %s-%s | Price: %.2f | Status: %s",
                bookingId, user.getName(), facility.getName(), facility.getType(),
                timeSlot.getDate(), timeSlot.getStartTime(), timeSlot.getEndTime(),
                price, status);
    }
}