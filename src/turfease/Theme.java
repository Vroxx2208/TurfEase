package turfease;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;

public class Theme {

    public static final Color GREEN = Color.decode("#16A34A");
    public static final Color GRAY = Color.decode("#6B7280");

    public static void apply() {
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#16A34A"));
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 999);
        UIManager.put("Component.arc", 16);
        UIManager.put("TextComponent.arc", 16);
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 15));
    }

    public static JButton button(String text, boolean primary) {
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

    public static JPanel header(String title, String subtitle) {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(GREEN);
        header.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel t = new JLabel(title);
        t.setForeground(Color.WHITE);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 28f));

        JLabel s = new JLabel(subtitle);
        s.setForeground(new Color(255, 255, 255, 210));

        header.add(t);
        header.add(s);
        return header;
    }
}