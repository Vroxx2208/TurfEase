package turfease;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BookingManager manager = new BookingManager();
    private static User currentUser;

    public static void main(String[] args) {
        seedSampleData();

        System.out.println("=== Welcome to TurfEase ===");
        System.out.print("Enter your name to continue: ");
        String name = scanner.nextLine();
        currentUser = new User(name, name.toLowerCase() + "@example.com", "0000000000");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listFacilities();
                    break;
                case "2":
                    bookASlot();
                    break;
                case "3":
                    currentUser.printBookingHistory();
                    break;
                case "4":
                    cancelABooking();
                    break;
                case "5":
                    running = false;
                    System.out.println("Thanks for using TurfEase!");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. View facilities and available slots");
        System.out.println("2. Book a slot");
        System.out.println("3. View my booking history");
        System.out.println("4. Cancel a booking");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    private static void listFacilities() {
        List<SportsFacility> facilities = manager.listAllFacilities();
        if (facilities.isEmpty()) {
            System.out.println("No facilities available yet.");
            return;
        }
        for (SportsFacility facility : facilities) {
            System.out.println(facility);
            List<TimeSlot> slots = facility.getTimeSlots();
            if (slots.isEmpty()) {
                System.out.println("   (no time slots added yet)");
            } else {
                for (TimeSlot slot : slots) {
                    System.out.println("   " + slot);
                }
            }
        }
    }

    private static void bookASlot() {
        listFacilities();
        System.out.print("Enter facility id to book (e.g. FAC1): ");
        String facilityId = scanner.nextLine().trim();
        System.out.print("Enter slot id to book (e.g. SLOT1): ");
        String slotId = scanner.nextLine().trim();

        manager.bookFacility(currentUser, facilityId, slotId);
    }

    private static void cancelABooking() {
        System.out.print("Enter booking id to cancel (e.g. BKG1): ");
        String bookingId = scanner.nextLine().trim();
        manager.cancelBooking(bookingId);
    }

    /**
     * Adds a few sample facilities and time slots so there is
     * something to browse and book right away, as if an admin
     * had already set the system up.
     */
    private static void seedSampleData() {
        Admin admin = new Admin("System Admin", "admin@turfease.com", "1111111111");

        SportsFacility football = new FootballTurf("Greenfield Football Turf", "Kannur", 800);
        SportsFacility cricket = new CricketTurf("City Cricket Nets", "Kannur", 1000);
        SportsFacility badminton = new BadmintonCourt("Ace Badminton Court", "Kannur", 400);
        SportsFacility pool = new SwimmingPool("Blue Wave Swimming Pool", "Kannur", 300);

        admin.addFacility(manager, football);
        admin.addFacility(manager, cricket);
        admin.addFacility(manager, badminton);
        admin.addFacility(manager, pool);

        LocalDate today = LocalDate.now();
        admin.addTimeSlot(manager, football.getFacilityId(), today, LocalTime.of(16, 0), LocalTime.of(17, 0));
        admin.addTimeSlot(manager, football.getFacilityId(), today, LocalTime.of(18, 0), LocalTime.of(19, 0));
        admin.addTimeSlot(manager, cricket.getFacilityId(), today, LocalTime.of(9, 0), LocalTime.of(11, 0));
        admin.addTimeSlot(manager, badminton.getFacilityId(), today, LocalTime.of(7, 0), LocalTime.of(8, 0));
        admin.addTimeSlot(manager, pool.getFacilityId(), today, LocalTime.of(10, 0), LocalTime.of(12, 0));
    }
}