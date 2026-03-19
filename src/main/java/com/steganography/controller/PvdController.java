package com.steganography.controller;

import com.steganography.model.SteganographyResult;
import com.steganography.service.PvdService;
import com.steganography.view.PvdPanel;

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
 * Wires the PVD panel's buttons to the PvdService.
 *
 * In the original Home.java, PVD logic was split across six event handlers:
 *   EncoderButton1ActionPerformed   — switch to encode mode
 *   DecoderButton1ActionPerformed   — switch to decode mode
 *   EncDecButton1ActionPerformed    — run LSB encode/decode (on PVD panel)
 *   EndecButtonActionPerformed      — run true PVD encode/decode
 *   ChooseImgButton1ActionPerformed — open image file chooser
 *   ChooseTxtButton1ActionPerformed — open text file chooser
 *   SaveButton1ActionPerformed      — save result
 *   ResetButton1ActionPerformed     — clear everything
 *
 * Note: the original PVD panel had TWO separate run buttons (EncDecButton1 and
 * EndecButton) placed at the same screen position. EncDecButton1 used the LSB
 * algorithm; EndecButton used the true PVD algorithm. In this refactor, the
 * single "run" button always uses the PVD algorithm, which is what the panel
 * is named after.
 */
public class PvdController {

    private final PvdService pvdService = new PvdService();
    private final PvdPanel   panel;

    private final JFileChooser imageChooser = new JFileChooser();
    private final JFileChooser textChooser  = new JFileChooser();
    private final JFileChooser saveImgChooser;
    private final JFileChooser saveTxtChooser;

    private BufferedImage originalImage;
    private BufferedImage stegoImage;
    private boolean       encodeMode = true;

    public PvdController(PvdPanel panel) {
        this.panel = panel;

        imageChooser.setFileFilter(
            new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        textChooser.setFileFilter(
            new FileNameExtensionFilter("Text files (*.txt)", "txt"));

        saveImgChooser = new JFileChooser();
        saveImgChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveImgChooser.setSelectedFile(new File("SteganoImg_PVD.png"));
        saveImgChooser.setFileFilter(new FileNameExtensionFilter("PNG file", "png"));

        saveTxtChooser = new JFileChooser();
        saveTxtChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveTxtChooser.setSelectedFile(new File("DecodedText_PVD.txt"));
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
    // Mode switching
    // Original: EncoderButton1ActionPerformed / DecoderButton1ActionPerformed
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
    // File choosers
    // Original: ChooseImgButton1ActionPerformed / ChooseTxtButton1ActionPerformed
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
    // Encode / Decode
    // Original: EndecButtonActionPerformed (true PVD algorithm)
    // -----------------------------------------------------------------------

    private void runEncodeOrDecode() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(panel, "Please choose an image first.");
            return;
        }

        if (encodeMode) {
            String message = panel.textArea.getText();
            SteganographyResult result = pvdService.encode(originalImage, message);

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
            SteganographyResult result = pvdService.decode(stegoImage);

            if (result.isSuccess()) {
                panel.textArea.setText(result.getDecodedText());
            } else {
                panel.textArea.setText("");
                JOptionPane.showMessageDialog(panel, result.getMessage());
            }
        }
    }

    // -----------------------------------------------------------------------
    // Save
    // Original: SaveButton1ActionPerformed
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
    // Reset
    // Original: ResetButton1ActionPerformed
    // -----------------------------------------------------------------------

    private void reset() {
        originalImage = null;
        stegoImage    = null;
        panel.originalImageLabel.setIcon(null);
        panel.stegoImageLabel.setIcon(null);
        panel.textArea.setText("");
    }
}
