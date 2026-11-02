package turfease;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingStore {

    // Saved in the folder the program is run from (your project folder in VS Code)
    private static final Path FILE = Paths.get("bookings.txt");

    public static void save(List<Booking> bookings) {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookings) {
            TimeSlot s = b.getTimeSlot();
            lines.add(String.join("|",
                    b.getBookingId(),
                    clean(b.getUser().getEmail()),
                    clean(b.getFacility().getName()),
                    s.getDate().toString(),
                    s.getStartTime().toString(),
                    s.getEndTime().toString(),
                    String.valueOf(b.getPrice()),
                    b.getBookedAt().toString(),
                    b.getStatus().name()));
        }
        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not save bookings: " + e.getMessage());
        }
    }

    public static void load(BookingManager manager) {
        if (!Files.exists(FILE)) {
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(FILE, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not read saved bookings: " + e.getMessage());
            return;
        }

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                String[] p = line.split("\\|");
                String id = p[0];
                String userEmail = p[1];
                String facilityName = p[2];
                LocalDate date = LocalDate.parse(p[3]);
                LocalTime start = LocalTime.parse(p[4]);
                LocalTime end = LocalTime.parse(p[5]);
                double price = Double.parseDouble(p[6]);
                LocalDateTime bookedAt = LocalDateTime.parse(p[7]);
                BookingStatus status = BookingStatus.valueOf(p[8]);

                User user = AppData.findUser(userEmail);
                if (user == null) {
                    continue; // that account no longer exists
                }

                SportsFacility facility = findFacilityByName(manager, facilityName);
                if (facility == null) {
                    continue; // that facility is no longer in the system
                }

                TimeSlot slot = findSlot(facility, date, start, end);
                if (slot == null) {
                    // an old slot that is no longer generated (e.g. a past day)
                    slot = new TimeSlot(date, start, end);
                }

                manager.restoreBooking(user, facility, slot, id, price, bookedAt, status);
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable line in bookings file: " + line);
            }
        }
    }

    private static SportsFacility findFacilityByName(BookingManager manager, String name) {
        for (SportsFacility f : manager.listAllFacilities()) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    private static TimeSlot findSlot(SportsFacility facility, LocalDate date,
                                     LocalTime start, LocalTime end) {
        for (TimeSlot s : facility.getTimeSlots()) {
            if (s.getDate().equals(date) && s.getStartTime().equals(start)
                    && s.getEndTime().equals(end)) {
                return s;
            }
        }
        return null;
    }

    private static String clean(String text) {
        return text.replace("|", " ");
    }
}