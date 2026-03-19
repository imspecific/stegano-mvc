package com.steganography.controller;

import com.steganography.service.AuthService;
import com.steganography.view.LoginPanel;
import com.steganography.view.MainFrame;
import com.steganography.view.RegisterPanel;

import javax.swing.*;

public class AuthController {

    private final AuthService   authService = new AuthService();
    private final LoginPanel    loginPanel;
    private final RegisterPanel registerPanel;
    private final MainFrame     frame;

    public AuthController(LoginPanel loginPanel, RegisterPanel registerPanel, MainFrame frame) {
        this.loginPanel    = loginPanel;
        this.registerPanel = registerPanel;
        this.frame         = frame;

        wireLoginPanel();
        wireRegisterPanel();
    }

    private void wireLoginPanel() {
        // Login button — original: jButton1ActionPerformed
        loginPanel.loginButton.addActionListener(e -> handleLogin());

        // Press Enter in password field also triggers login
        loginPanel.passwordField.addActionListener(e -> handleLogin());

        // "Sign up?" link — original: jLabel13MouseClicked
        loginPanel.signUpLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                frame.showRegisterPanel();
            }
        });
    }

    private void handleLogin() {
        String username = loginPanel.usernameField.getText();
        String password = new String(loginPanel.passwordField.getPassword());

        AuthService.LoginResult result = authService.login(username, password);

        if (result.isSuccess()) {

            loginPanel.usernameField.setText("");
            loginPanel.passwordField.setText("");
            JOptionPane.showMessageDialog(frame, result.getMessage());
            frame.onLoginSuccess();
        } else {
            JOptionPane.showMessageDialog(frame, result.getMessage());
        }
    }
    private void wireRegisterPanel() {
        registerPanel.registerButton.addActionListener(e -> handleRegister());
    }

    private void handleRegister() {
        String firstName      = registerPanel.firstNameField.getText();
        String lastName       = registerPanel.lastNameField.getText();
        String username       = registerPanel.usernameField.getText();
        String password       = new String(registerPanel.passwordField.getPassword());
        String confirmPassword = new String(registerPanel.confirmPassField.getPassword());

        String message = authService.register(firstName, lastName, username,
                                               password, confirmPassword);
        JOptionPane.showMessageDialog(frame, message);

        if (message.startsWith("Sign up successful")) {
            registerPanel.firstNameField.setText("");
            registerPanel.lastNameField.setText("");
            registerPanel.usernameField.setText("");
            registerPanel.passwordField.setText("");
            registerPanel.confirmPassField.setText("");
            frame.showContent("login");
        }
    }
}
