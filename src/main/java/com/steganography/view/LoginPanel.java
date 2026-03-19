package com.steganography.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The login form panel.
 *
 * In the original Home.java, this was "LoginPanel" built inside initComponents()
 * with AbsoluteLayout and dozens of inline field settings.  The Swing components
 * are the same; they are just now in their own class.
 *
 * The controller (AuthController) wires all the button listeners.
 * This class is a pure view — it only creates and exposes components.
 */
public class LoginPanel extends JPanel {

    // Components exposed to AuthController
    public final JTextField     usernameField   = new JTextField("Enter username");
    public final JPasswordField passwordField   = new JPasswordField();
    public final JButton        loginButton     = new JButton("Login");
    public final JLabel         signUpLink      = new JLabel("Sign up?");

    public LoginPanel() {
        setLayout(null);
        setBackground(new Color(40, 40, 40));
        setMinimumSize(new Dimension(830, 630));
        setPreferredSize(new Dimension(830, 630));

        // Title
        JLabel title = new JLabel("Login");
        title.setFont(new Font("Tahoma", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(110, 90, 199, 50);
        add(title);

        // Username label + field + separator
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(330, 210, 200, 30);
        add(userLabel);

        usernameField.setBackground(new Color(40, 40, 40));
        usernameField.setFont(new Font("Tahoma", Font.BOLD, 14));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBorder(null);
        usernameField.setBounds(330, 250, 276, 25);
        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { usernameField.selectAll(); }
        });
        add(usernameField);

        JSeparator sep1 = new JSeparator();
        sep1.setBounds(330, 275, 276, 10);
        add(sep1);

        // Password label + field + separator
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(330, 300, 200, 30);
        add(passLabel);

        passwordField.setBackground(new Color(40, 40, 40));
        passwordField.setFont(new Font("Tahoma", Font.BOLD, 14));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBorder(null);
        passwordField.setBounds(330, 340, 276, 25);
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { passwordField.selectAll(); }
        });
        add(passwordField);

        JSeparator sep2 = new JSeparator();
        sep2.setBounds(330, 365, 276, 10);
        add(sep2);

        // Sign-up link
        signUpLink.setFont(new Font("Tahoma", Font.BOLD, 13));
        signUpLink.setForeground(Color.WHITE);
        signUpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLink.setBounds(330, 420, 80, 20);
        add(signUpLink);

        // Login button
        loginButton.setBorder(null);
        loginButton.setBounds(530, 410, 80, 28);
        add(loginButton);

        // Background image
        addBackgroundImage();
    }

    private void addBackgroundImage() {
        try {
            Image img = ImageIO.read(getClass().getResourceAsStream("/Images/BaseBackground.jpg"));
            if (img != null) {
                JLabel bg = new JLabel(new ImageIcon(img));
                bg.setBounds(0, 0, 900, 640);
                bg.setOpaque(true);
                add(bg);
            }
        } catch (IOException | NullPointerException ignored) {}
    }
}
