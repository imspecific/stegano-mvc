package com.steganography.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The PVD steganography panel.
 * Mirrors the original Home.java "PVD" JPanel from initComponents().
 * The controller (PvdController) wires all listeners.
 */
public class PvdPanel extends JPanel {

    public final JButton encodeModeButton = new JButton("Encode");
    public final JButton decodeModeButton = new JButton("Decode");

    public final JButton chooseImageButton = new JButton("Choose Image");
    public final JButton chooseTextButton  = new JButton("Choose text file");
    public final JButton runButton         = new JButton("Encode");
    public final JButton saveButton        = new JButton("Save Image File");
    public final JButton resetButton       = new JButton("Reset");

    public final JLabel    originalImageLabel = new JLabel();
    public final JLabel    stegoImageLabel    = new JLabel();
    public final JTextArea textArea           = new JTextArea();
    public final JLabel    textAreaLabel      = new JLabel("Input text:");

    public PvdPanel() {
        setLayout(null);
        setBackground(new Color(40, 40, 40));

        encodeModeButton.setBackground(new Color(60, 160, 180));
        encodeModeButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        encodeModeButton.setForeground(Color.WHITE);
        encodeModeButton.setBounds(33, 13, 135, 30);
        add(encodeModeButton);

        decodeModeButton.setBackground(new Color(100, 100, 100));
        decodeModeButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        decodeModeButton.setForeground(Color.WHITE);
        decodeModeButton.setBounds(185, 13, 135, 30);
        add(decodeModeButton);

        originalImageLabel.setBackground(new Color(60, 60, 60));
        originalImageLabel.setOpaque(true);
        originalImageLabel.setBounds(33, 65, 400, 350);
        add(originalImageLabel);

        stegoImageLabel.setBackground(new Color(60, 60, 60));
        stegoImageLabel.setOpaque(true);
        stegoImageLabel.setBounds(467, 65, 400, 350);
        add(stegoImageLabel);

        textAreaLabel.setForeground(Color.WHITE);
        textAreaLabel.setBounds(30, 435, 120, 20);
        add(textAreaLabel);

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBounds(30, 455, 834, 95);
        add(scroll);

        chooseImageButton.setBounds(30, 580, 130, 26);
        add(chooseImageButton);

        chooseTextButton.setBounds(180, 580, 130, 26);
        add(chooseTextButton);

        runButton.setBounds(410, 580, 80, 26);
        add(runButton);

        saveButton.setBounds(630, 580, 130, 26);
        add(saveButton);

        resetButton.setBounds(780, 580, 80, 26);
        add(resetButton);

        addBackgroundImage();
    }

    public void resetToEncodeMode() {
        encodeModeButton.setBackground(new Color(60, 160, 180));
        decodeModeButton.setBackground(new Color(100, 100, 100));
        runButton.setText("Encode");
        saveButton.setText("Save Image File");
        textAreaLabel.setText("Input text:");
        stegoImageLabel.setVisible(true);
        chooseTextButton.setVisible(true);
        originalImageLabel.setIcon(null);
        stegoImageLabel.setIcon(null);
        textArea.setText("");
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
