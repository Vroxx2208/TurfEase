package turfease;

import javax.swing.*;
import java.awt.*;

public class HomeScreen {

    // The app starts here: load everything, then show the login screen
    public static void main(String[] args) {
        Theme.apply();
        AppData.start();
        SwingUtilities.invokeLater(LoginScreen::show);
    }

    // The customer's home window (shown after signing in or registering)
    public static void showHome() {
        JFrame frame = new JFrame("TurfEase");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 460);
        frame.setLocationRelativeTo(null);

        JPanel body = new JPanel(new GridLayout(3, 1, 0, 14));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JButton book = Theme.button("Book a Facility", true);
        JButton view = Theme.button("My Bookings", false);
        JButton logout = Theme.button("Log Out", false);

        book.addActionListener(e -> new BookingScreen(frame).setVisible(true));
        view.addActionListener(e -> new MyBookingsScreen(frame).setVisible(true));
        logout.addActionListener(e -> {
            frame.dispose();
            AppData.logout();
            LoginScreen.show();
        });

        body.add(book);
        body.add(view);
        body.add(logout);

        frame.add(Theme.header("TurfEase",
                "Welcome, " + AppData.getCurrentUser().getName()), BorderLayout.NORTH);
        frame.add(body, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}