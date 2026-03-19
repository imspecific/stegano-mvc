package com.steganography.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The LSB steganography panel.
 *
 * In the original Home.java this was the "LSB" JPanel built inside initComponents().
 * It contained Orig_imgLabel, Stegano_imgLabel, jTextArea1, and all the buttons.
 *
 * The controller (LsbController) wires all listeners.
 * This class is a pure view.
 */
public class LsbPanel extends JPanel {

    // --- Mode toggle buttons (Encode / Decode) ---
    public final JButton encodeModeButton = new JButton("Encode");
    public final JButton decodeModeButton = new JButton("Decode");

    // --- Action buttons ---
    public final JButton chooseImageButton = new JButton("Choose Image");
    public final JButton chooseTextButton  = new JButton("Choose text file");
    public final JButton runButton         = new JButton("Encode");   // label changes with mode
    public final JButton saveButton        = new JButton("Save Image File");
    public final JButton resetButton       = new JButton("Reset");

    // --- Display areas ---
    public final JLabel    originalImageLabel = new JLabel();
    public final JLabel    stegoImageLabel    = new JLabel();
    public final JTextArea textArea           = new JTextArea();
    public final JLabel    textAreaLabel      = new JLabel("Input text:");

    public LsbPanel() {
        setLayout(null);
        setBackground(new Color(40, 40, 40));

        // Mode buttons
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

        // Image display panels
        originalImageLabel.setBackground(new Color(60, 60, 60));
        originalImageLabel.setOpaque(true);
        originalImageLabel.setBounds(33, 65, 400, 350);
        add(originalImageLabel);

        stegoImageLabel.setBackground(new Color(60, 60, 60));
        stegoImageLabel.setOpaque(true);
        stegoImageLabel.setBounds(467, 65, 400, 350);
        add(stegoImageLabel);

        // Text area label + scroll pane
        textAreaLabel.setForeground(Color.WHITE);
        textAreaLabel.setBounds(30, 435, 100, 20);
        add(textAreaLabel);

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBounds(30, 455, 834, 95);
        add(scroll);

        // Bottom action buttons
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

    /** Puts the panel back into Encode mode — called when the nav item is clicked. */
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
