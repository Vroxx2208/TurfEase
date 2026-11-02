package turfease;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserStore {

    private static final Path FILE = Paths.get("users.txt");

    public static List<Person> load() {
        List<Person> people = new ArrayList<>();
        if (!Files.exists(FILE)) {
            return people;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(FILE, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not read users: " + e.getMessage());
            return people;
        }

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                String[] p = line.split("\\|", -1);
                String role = p[0];
                String email = p[1];
                String name = p[2];
                String phone = p[3];
                Person person = role.equals("ADMIN")
                        ? new Admin(name, email, phone)
                        : new User(name, email, phone);
                person.setCredentials(p[4], p[5]);
                people.add(person);
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable user line");
            }
        }
        return people;
    }

    public static void save(Collection<Person> people) {
        List<String> lines = new ArrayList<>();
        for (Person p : people) {
            lines.add(String.join("|",
                    p.getRole(),
                    clean(p.getEmail()),
                    clean(p.getName()),
                    clean(p.getPhone()),
                    p.getPasswordSalt(),
                    p.getPasswordHash()));
        }
        try {
            Files.write(FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not save users: " + e.getMessage());
        }
    }

    private static String clean(String text) {
        return text.replace("|", " ");
    }
}