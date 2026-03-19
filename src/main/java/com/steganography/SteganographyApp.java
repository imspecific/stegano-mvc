package com.steganography;

import com.steganography.view.MainFrame;

import javax.swing.*;

public class SteganographyApp {

    public static void main(String[] args) {
        applyNimbusLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private static void applyNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception e) {
            // Error
            System.err.println("Nimbus L&F not available, using default: " + e.getMessage());
        }
    }
}
