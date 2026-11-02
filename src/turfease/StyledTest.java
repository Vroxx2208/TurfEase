package turfease;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public class StyledTest {

    private static final Color GREEN = Color.decode("#16A34A");

    public static void main(String[] args) {
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#16A34A"));
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 999);
        UIManager.put("Component.arc", 16);
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 15));

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TurfEase");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(420, 440);
            frame.setLocationRelativeTo(null);

            // Green header banner
            JPanel header = new JPanel(new GridLayout(2, 1));
            header.setBackground(GREEN);
            header.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

            JLabel title = new JLabel("TurfEase");
            title.setForeground(Color.WHITE);
            title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

            JLabel subtitle = new JLabel("Book your game in a few clicks");
            subtitle.setForeground(new Color(255, 255, 255, 210));

            header.add(title);
            header.add(subtitle);

            // White body with action buttons
            JPanel body = new JPanel(new GridLayout(3, 1, 0, 14));
            body.setBackground(Color.WHITE);
            body.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

            JButton book = makeButton("Book a Facility", true);
            JButton view = makeButton("View Bookings", false);
            JButton cancel = makeButton("Cancel Booking", false);

            book.addActionListener(e ->
                    JOptionPane.showMessageDialog(frame, "Booking screen coming soon!"));

            body.add(book);
            body.add(view);
            body.add(cancel);

            frame.add(header, BorderLayout.NORTH);
            frame.add(body, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    private static JButton makeButton(String text, boolean primary) {
        JButton b = new JButton(text);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 15f));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (primary) {
            b.setBackground(GREEN);
            b.setForeground(Color.WHITE);
        }
        return b;
    }
}