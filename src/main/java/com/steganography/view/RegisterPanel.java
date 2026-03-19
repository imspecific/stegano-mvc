package com.steganography.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The registration form panel.
 * Mirrors the original Home.java RegisterPanel from initComponents().
 * All listener wiring is done by AuthController.
 */
public class RegisterPanel extends JPanel {

    public final JTextField     firstNameField    = new JTextField("Enter first name");
    public final JTextField     lastNameField     = new JTextField("Enter last name");
    public final JTextField     usernameField     = new JTextField("Enter username");
    public final JPasswordField passwordField     = new JPasswordField();
    public final JPasswordField confirmPassField  = new JPasswordField();
    public final JButton        registerButton    = new JButton("Register");

    public RegisterPanel() {
        setLayout(null);
        setBackground(new Color(40, 40, 40));

        addLabeledField("First Name",        firstNameField,   39,  90);
        addLabeledField("Last Name",         lastNameField,   160, 210);
        addLabeledField("Username",          usernameField,   280, 330);
        addLabeledPasswordField("Password",  passwordField,   400, 450);
        addLabeledPasswordField("Confirm Password", confirmPassField, 490, 540);

        registerButton.setBorder(null);
        registerButton.setBounds(530, 575, 80, 28);
        add(registerButton);

        addBackgroundImage();
    }

    private void addLabeledField(String labelText, JTextField field, int labelY, int fieldY) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Tahoma", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setBounds(377, labelY, 200, 30);
        add(label);

        field.setBackground(new Color(40, 40, 40));
        field.setFont(new Font("Tahoma", Font.BOLD, 14));
        field.setForeground(Color.WHITE);
        field.setBorder(null);
        field.setBounds(377, fieldY, 276, 25);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { field.selectAll(); }
        });
        add(field);

        JSeparator sep = new JSeparator();
        sep.setBounds(377, fieldY + 25, 276, 10);
        add(sep);
    }

    private void addLabeledPasswordField(String labelText, JPasswordField field, int labelY, int fieldY) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Tahoma", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setBounds(377, labelY, 250, 30);
        add(label);

        field.setBackground(new Color(40, 40, 40));
        field.setFont(new Font("Tahoma", Font.BOLD, 14));
        field.setForeground(Color.WHITE);
        field.setBorder(null);
        field.setBounds(377, fieldY, 276, 25);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { field.selectAll(); }
        });
        add(field);

        JSeparator sep = new JSeparator();
        sep.setBounds(377, fieldY + 25, 276, 10);
        add(sep);
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
