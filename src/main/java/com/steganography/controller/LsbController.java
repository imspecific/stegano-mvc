package com.steganography.controller;

import com.steganography.model.SteganographyResult;
import com.steganography.service.LsbService;
import com.steganography.view.LsbPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Wires the LSB panel's buttons to the LsbService.
 *
 * In the original Home.java, this logic was spread across six event handlers:
 *   EncoderButtonActionPerformed   — switch to encode mode
 *   DecoderButtonActionPerformed   — switch to decode mode
 *   EncDecButtonActionPerformed    — run encode or decode
 *   ChooseImgButtonActionPerformed — open image file chooser
 *   ChooseTxtButtonActionPerformed — open text file chooser
 *   SaveButtonActionPerformed      — save result image or text
 *   ResetButtonActionPerformed     — clear everything
 *
 * Each method is now a small, clearly-named private method here.
 */
public class LsbController {

    private final LsbService lsbService = new LsbService();
    private final LsbPanel   panel;

    // Shared file choosers (mirrors original choose_img / choose_txt / save_img / save_txt)
    private final JFileChooser imageChooser = new JFileChooser();
    private final JFileChooser textChooser  = new JFileChooser();
    private final JFileChooser saveImgChooser;
    private final JFileChooser saveTxtChooser;

    // State
    private BufferedImage originalImage; // loaded cover or stego image
    private BufferedImage stegoImage;    // result after encoding
    private boolean       encodeMode = true; // true = encode, false = decode

    public LsbController(LsbPanel panel) {
        this.panel = panel;

        // Configure file choosers (same filters as original)
        imageChooser.setFileFilter(
            new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        textChooser.setFileFilter(
            new FileNameExtensionFilter("Text files (*.txt)", "txt"));

        saveImgChooser = new JFileChooser();
        saveImgChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveImgChooser.setSelectedFile(new File("SteganoImg.png"));
        saveImgChooser.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));

        saveTxtChooser = new JFileChooser();
        saveTxtChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveTxtChooser.setSelectedFile(new File("DecodedText.txt"));
        saveTxtChooser.setFileFilter(new FileNameExtensionFilter("Text file", "txt"));

        wireButtons();
    }

    private void wireButtons() {
        panel.encodeModeButton.addActionListener(e -> switchToEncodeMode());
        panel.decodeModeButton.addActionListener(e -> switchToDecodeMode());
        panel.chooseImageButton.addActionListener(e -> chooseImage());
        panel.chooseTextButton.addActionListener(e -> chooseTextFile());
        panel.runButton.addActionListener(e -> runEncodeOrDecode());
        panel.saveButton.addActionListener(e -> saveResult());
        panel.resetButton.addActionListener(e -> reset());
    }

    // -----------------------------------------------------------------------
    // Mode switching — original: EncoderButtonActionPerformed / DecoderButtonActionPerformed
    // -----------------------------------------------------------------------

    private void switchToEncodeMode() {
        encodeMode = true;
        panel.encodeModeButton.setBackground(new Color(60, 160, 180));
        panel.decodeModeButton.setBackground(new Color(100, 100, 100));
        panel.runButton.setText("Encode");
        panel.saveButton.setText("Save Image File");
        panel.textAreaLabel.setText("Input text:");
        panel.stegoImageLabel.setVisible(true);
        panel.chooseTextButton.setVisible(true);
        reset();
    }

    private void switchToDecodeMode() {
        encodeMode = false;
        panel.decodeModeButton.setBackground(new Color(60, 160, 180));
        panel.encodeModeButton.setBackground(new Color(100, 100, 100));
        panel.runButton.setText("Decode");
        panel.saveButton.setText("Save Text File");
        panel.textAreaLabel.setText("Output text:");
        panel.stegoImageLabel.setVisible(false);
        panel.chooseTextButton.setVisible(false);
        reset();
    }

    // -----------------------------------------------------------------------
    // File choosers — original: ChooseImgButtonActionPerformed / ChooseTxtButtonActionPerformed
    // -----------------------------------------------------------------------

    private void chooseImage() {
        int result = imageChooser.showOpenDialog(panel);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                originalImage = ImageIO.read(imageChooser.getSelectedFile());
                stegoImage    = ImageIO.read(imageChooser.getSelectedFile());
                Image scaled  = originalImage.getScaledInstance(
                    panel.originalImageLabel.getWidth(),
                    panel.originalImageLabel.getHeight(),
                    Image.SCALE_SMOOTH);
                panel.originalImageLabel.setIcon(new ImageIcon(scaled));
                if (!encodeMode) panel.textArea.setText("");
                JOptionPane.showMessageDialog(panel, "File successfully loaded");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(panel, "Error loading image");
            }
        } else {
            JOptionPane.showMessageDialog(panel, "No file chosen!");
        }
    }

    private void chooseTextFile() {
        int result = textChooser.showOpenDialog(panel);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (Scanner scanner = new Scanner(textChooser.getSelectedFile())) {
                panel.textArea.setText(scanner.useDelimiter("\\A").next());
                JOptionPane.showMessageDialog(panel, "File successfully loaded");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(panel, "File not found");
            }
        } else {
            JOptionPane.showMessageDialog(panel, "No file chosen!");
        }
    }

    // -----------------------------------------------------------------------
    // Encode / Decode — original: EncDecButtonActionPerformed
    // -----------------------------------------------------------------------

    private void runEncodeOrDecode() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(panel, "Please choose an image first.");
            return;
        }

        if (encodeMode) {
            // Encode
            String message = panel.textArea.getText();
            SteganographyResult result = lsbService.encode(originalImage, message);

            if (result.isSuccess()) {
                stegoImage = result.getStegoImage();
                Image scaled = stegoImage.getScaledInstance(
                    panel.stegoImageLabel.getWidth(),
                    panel.stegoImageLabel.getHeight(),
                    Image.SCALE_SMOOTH);
                panel.stegoImageLabel.setIcon(new ImageIcon(scaled));
            } else {
                JOptionPane.showMessageDialog(panel, result.getMessage());
            }
        } else {
            // Decode
            SteganographyResult result = lsbService.decode(stegoImage);

            if (result.isSuccess()) {
                panel.textArea.setText(result.getDecodedText());
            } else {
                panel.textArea.setText("");
                JOptionPane.showMessageDialog(panel, result.getMessage());
            }
        }
    }

    // -----------------------------------------------------------------------
    // Save — original: SaveButtonActionPerformed
    // -----------------------------------------------------------------------

    private void saveResult() {
        if (encodeMode) {
            int choice = saveImgChooser.showSaveDialog(panel);
            if (choice == JFileChooser.APPROVE_OPTION) {
                try {
                    ImageIO.write(stegoImage, "png", saveImgChooser.getSelectedFile());
                    JOptionPane.showMessageDialog(panel, "File successfully saved");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(panel, "Error saving file");
                }
            }
        } else {
            int choice = saveTxtChooser.showSaveDialog(panel);
            if (choice == JFileChooser.APPROVE_OPTION) {
                try (FileWriter fw = new FileWriter(saveTxtChooser.getSelectedFile())) {
                    fw.write(panel.textArea.getText());
                    JOptionPane.showMessageDialog(panel, "File successfully saved");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(panel, "Error saving file");
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Reset — original: ResetButtonActionPerformed
    // -----------------------------------------------------------------------

    private void reset() {
        originalImage = null;
        stegoImage    = null;
        panel.originalImageLabel.setIcon(null);
        panel.stegoImageLabel.setIcon(null);
        panel.textArea.setText("");
    }
}
