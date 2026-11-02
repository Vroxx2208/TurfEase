package turfease;

import javax.swing.*;
import java.awt.*;

public class RegisterScreen extends JDialog {

    public RegisterScreen(JFrame parent) {
        super(parent, "Create Account", true);
        setSize(440, 580);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        JTextField name = field("Full name");
        JTextField email = field("Email");
        JTextField phone = field("Phone (10 digits)");
        JPasswordField password = passwordField("Password (at least 6 characters)");
        JPasswordField confirm = passwordField("Confirm password");

        JLabel error = new JLabel(" ");
        error.setForeground(Color.decode("#DC2626"));

        JButton create = Theme.button("Create Account", true);
        JButton back = Theme.button("Back to Sign In", false);

        JPanel body = new JPanel(new GridLayout(0, 1, 0, 8));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        body.add(name);
        body.add(email);
        body.add(phone);
        body.add(password);
        body.add(confirm);
        body.add(error);
        body.add(create);
        body.add(back);

        create.addActionListener(e -> {
            String pw = new String(password.getPassword());
            if (!pw.equals(new String(confirm.getPassword()))) {
                error.setText("Passwords do not match.");
                return;
            }
            try {
                AppData.register(name.getText(), email.getText(), phone.getText(), pw);
                dispose();
                parent.dispose();
                HomeScreen.showHome();
            } catch (AuthException ex) {
                error.setText(ex.getMessage());
            }
        });
        back.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(create);

        add(Theme.header("Create Account", "Join TurfEase in a minute"), BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    private static JTextField field(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty("JTextField.placeholderText", placeholder);
        return f;
    }

    private static JPasswordField passwordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.putClientProperty("JPasswordField.showRevealButton", true);
        return f;
    }
}