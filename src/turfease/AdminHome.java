package turfease;

import javax.swing.*;
import java.awt.*;

public class AdminHome {

    public static void show() {
        JFrame frame = new JFrame("TurfEase Admin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 540);
        frame.setLocationRelativeTo(null);

        JLabel active = statValue();
        JLabel income = statValue();
        JLabel places = statValue();

        Runnable refresh = () -> {
            int confirmed = 0;
            double total = 0;
            for (Booking b : AppData.manager.viewAllBookings()) {
                if (b.getStatus() == BookingStatus.CONFIRMED) {
                    confirmed++;
                    total += b.getPrice();
                }
            }
            active.setText(String.valueOf(confirmed));
            income.setText(String.format("\u20B9%.0f", total));
            places.setText(String.valueOf(AppData.manager.listAllFacilities().size()));
        };
        refresh.run();

        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setBackground(Color.WHITE);
        stats.add(statBox(active, "Active bookings"));
        stats.add(statBox(income, "Booked value"));
        stats.add(statBox(places, "Facilities"));

        JButton bookings = Theme.button("All Bookings", true);
        JButton facilities = Theme.button("Manage Facilities", true);
        JButton logout = Theme.button("Log Out", false);

        bookings.addActionListener(e -> {
            new AdminBookingsScreen(frame).setVisible(true);
            refresh.run();
        });
        facilities.addActionListener(e -> {
            new AdminFacilitiesScreen(frame).setVisible(true);
            refresh.run();
        });
        logout.addActionListener(e -> {
            frame.dispose();
            AppData.logout();
            LoginScreen.show();
        });

        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 12));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));
        buttons.add(bookings);
        buttons.add(facilities);
        buttons.add(logout);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        body.add(stats, BorderLayout.NORTH);
        body.add(buttons, BorderLayout.CENTER);

        frame.add(Theme.header("Admin Panel",
                "Signed in as " + AppData.getCurrentPerson().getName()), BorderLayout.NORTH);
        frame.add(body, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JLabel statValue() {
        JLabel l = new JLabel("0", SwingConstants.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 26f));
        l.setForeground(Theme.GREEN);
        l.setBorder(BorderFactory.createEmptyBorder(12, 0, 2, 0));
        return l;
    }

    private static JPanel statBox(JLabel value, String caption) {
        JLabel c = new JLabel(caption, SwingConstants.CENTER);
        c.setForeground(Theme.GRAY);
        c.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(Color.decode("#D6DDD9")));
        p.add(value, BorderLayout.CENTER);
        p.add(c, BorderLayout.SOUTH);
        return p;
    }
}