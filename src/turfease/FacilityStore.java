package turfease;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FacilityStore {

    private static final Path FILE = Paths.get("facilities.txt");

    public static List<SportsFacility> load() {
        List<SportsFacility> list = new ArrayList<>();
        if (!Files.exists(FILE)) {
            return list;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(FILE, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not read facilities: " + e.getMessage());
            return list;
        }

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                String[] p = line.split("\\|");
                FacilityType type = FacilityType.valueOf(p[0]);
                double rate = Double.parseDouble(p[3]);
                list.add(create(type, p[1], p[2], rate));
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable facility line: " + line);
            }
        }
        return list;
    }

    public static void save(List<SportsFacility> facilities) {
        List<String> lines = new ArrayList<>();
        for (SportsFacility f : facilities) {
            lines.add(String.join("|",
                    f.getType().name(),
                    clean(f.getName()),
                    clean(f.getLocation()),
                    String.valueOf(f.getBaseRatePerHour())));
        }
        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not save facilities: " + e.getMessage());
        }
    }

    // Factory method: builds the right subclass for a facility type
    public static SportsFacility create(FacilityType type, String name, String location, double rate) {
        switch (type) {
            case FOOTBALL_TURF:
                return new FootballTurf(name, location, rate);
            case CRICKET_TURF:
                return new CricketTurf(name, location, rate);
            case BADMINTON_COURT:
                return new BadmintonCourt(name, location, rate);
            case SWIMMING_POOL:
                return new SwimmingPool(name, location, rate);
            default:
                throw new IllegalArgumentException("Unknown facility type: " + type);
        }
    }

    private static String clean(String text) {
        return text.replace("|", " ");
    }
}