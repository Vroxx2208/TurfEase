package turfease;

import javax.swing.*;
import java.awt.*;

public class LoginScreen {

    public static void show() {
        JFrame frame = new JFrame("TurfEase");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(440, 560);
        frame.setLocationRelativeTo(null);

        JTextField email = new JTextField();
        email.putClientProperty("JTextField.placeholderText", "you@example.com");
        JPasswordField password = new JPasswordField();
        password.putClientProperty("JTextField.placeholderText", "Password");
        password.putClientProperty("JPasswordField.showRevealButton", true);

        JLabel error = new JLabel(" ");
        error.setForeground(Color.decode("#DC2626"));

        JButton signIn = Theme.button("Sign In", true);
        JButton create = Theme.button("Create Account", false);

        JPanel body = new JPanel(new GridLayout(0, 1, 0, 8));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        body.add(label("Email"));
        body.add(email);
        body.add(label("Password"));
        body.add(password);
        body.add(error);
        body.add(signIn);
        body.add(create);

        signIn.addActionListener(e -> {
            try {
                AppData.login(email.getText(), new String(password.getPassword()));
                frame.dispose();
                if (AppData.isAdmin()) {
                    AdminHome.show();
                } else {
                    HomeScreen.showHome();
                }
            } catch (AuthException ex) {
                error.setText(ex.getMessage());
            }
        });
        create.addActionListener(e -> new RegisterScreen(frame).setVisible(true));
        frame.getRootPane().setDefaultButton(signIn);

        frame.add(Theme.header("TurfEase", "Sign in to book your game"), BorderLayout.NORTH);
        frame.add(body, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.GRAY);
        return l;
    }
}