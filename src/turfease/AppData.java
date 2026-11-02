package turfease;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppData {

    public static final BookingManager manager = new BookingManager();

    // everyone with an account, keyed by lower-case email
    private static final Map<String, Person> people = new LinkedHashMap<>();
    private static Person currentPerson;
    private static boolean started = false;

    // How many days of slots to create, starting from today
    private static final int DAYS_AHEAD = 2;

    // ---------- start up ----------

    public static void start() {
        if (started) return;
        started = true;

        for (Person p : UserStore.load()) {
            people.put(key(p.getEmail()), p);
        }
        if (!hasAdmin()) {
            Admin admin = new Admin("System Admin", "admin@turfease.com", "0000000000");
            admin.setPassword("admin123");
            people.put(key(admin.getEmail()), admin);
            saveUsers();
        }

        List<SportsFacility> facilities = FacilityStore.load();
        if (facilities.isEmpty()) {
            facilities = defaultFacilities();
            FacilityStore.save(facilities);
        }
        for (SportsFacility f : facilities) {
            registerFacility(f);
        }

        BookingStore.load(manager);
    }

    // ---------- accounts ----------

    public static void login(String email, String password) throws AuthException {
        Person p = people.get(key(email));
        if (p == null || !p.checkPassword(password)) {
            throw new AuthException("Wrong email or password.");
        }
        currentPerson = p;
    }

    public static void register(String name, String email, String phone, String password)
            throws AuthException {
        name = name.trim();
        email = key(email);
        phone = phone.trim();

        if (name.isEmpty()) {
            throw new AuthException("Please enter your name.");
        }
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new AuthException("Please enter a valid email address.");
        }
        if (!phone.matches("\\d{10}")) {
            throw new AuthException("Phone number must be 10 digits.");
        }
        if (password.length() < 6) {
            throw new AuthException("Password must be at least 6 characters.");
        }
        if (people.containsKey(email)) {
            throw new AuthException("An account with this email already exists.");
        }

        User user = new User(name, email, phone);
        user.setPassword(password);
        people.put(email, user);
        saveUsers();
        currentPerson = user;
    }

    public static void logout() {
        currentPerson = null;
    }

    public static Person getCurrentPerson() {
        return currentPerson;
    }

    public static boolean isAdmin() {
        return currentPerson instanceof Admin;
    }

    // The logged-in customer (null if an admin is logged in)
    public static User getCurrentUser() {
        return currentPerson instanceof User ? (User) currentPerson : null;
    }

    // Used by BookingStore to link a saved booking to its owner
    public static User findUser(String email) {
        Person p = people.get(key(email));
        return p instanceof User ? (User) p : null;
    }

    private static boolean hasAdmin() {
        for (Person p : people.values()) {
            if (p instanceof Admin) {
                return true;
            }
        }
        return false;
    }

    private static String key(String email) {
        return email.trim().toLowerCase();
    }

    // ---------- saving ----------

    public static void save() {
        BookingStore.save(manager.viewAllBookings());
    }

    private static void saveUsers() {
        UserStore.save(people.values());
    }

    public static void saveFacilities() {
        FacilityStore.save(manager.listAllFacilities());
    }

    // ---------- facilities and slots ----------

    // Adds a new facility (from the admin screen), creates its slots and saves it
    public static void addFacility(SportsFacility f) {
        registerFacility(f);
        saveFacilities();
    }

    private static void registerFacility(SportsFacility f) {
        manager.addFacility(f);
        for (int d = 0; d < DAYS_AHEAD; d++) {
            addSlotsFor(f, LocalDate.now().plusDays(d));
        }
    }

    // NOTE: the prices (per hour) below are PLACEHOLDERS - admins can change them in the app
    private static List<SportsFacility> defaultFacilities() {
        List<SportsFacility> list = new ArrayList<>();
        list.add(new FootballTurf("Kickathlon", "Painkanur", 800));
        list.add(new FootballTurf("Rungrado Turf", "Kaladithara, Edappal", 800));
        list.add(new FootballTurf("Oakland Sports Arena", "Moodal, Kuttippuram", 800));
        list.add(new FootballTurf("Footy Arena", "Valanchery", 800));
        list.add(new FootballTurf("Soccer 7", "Thonikkal, Valanchery", 800));
        list.add(new FootballTurf("Highbury Football Turf", "Kumbidi", 800));
        list.add(new FootballTurf("Eight and Four", "Tavanur", 800));
        list.add(new FootballTurf("Bobsleige Football & Cricket Turf", "Kandanakam", 800));
        list.add(new BadmintonCourt("Seeyen Badminton Indoor Court", "Kumbidi", 400));
        list.add(new BadmintonCourt("Edappal Indoor Stadium", "Sukapuram, Edappal", 400));
        list.add(new BadmintonCourt("Play City Indoor Badminton Court", "Changaramkulam", 400));
        return list;
    }

    private static void addSlotsFor(SportsFacility f, LocalDate date) {
        if (f instanceof FootballTurf || f instanceof CricketTurf) {
            addHourlySlots(f, date, 6, 8);    // morning: 6-7, 7-8
            addHourlySlots(f, date, 16, 22);  // evening: 4-5 ... 9-10
        } else {
            addHourlySlots(f, date, 10, 18);  // indoor / pools: 10 AM - 6 PM
        }
    }

    private static void addHourlySlots(SportsFacility f, LocalDate date, int firstStartHour, int lastEndHour) {
        for (int h = firstStartHour; h < lastEndHour; h++) {
            manager.addTimeSlot(f.getFacilityId(), date, LocalTime.of(h, 0), LocalTime.of(h + 1, 0));
        }
    }
}