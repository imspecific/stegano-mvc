package com.steganography.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The Info panel — displays explanations of LSB and PVD algorithms.
 *
 * In the original Home.java this was the "INFO" JPanel.
 * The text content is preserved verbatim from jTextArea3 and jTextArea4.
 * No controller is needed — this is a purely static informational panel.
 */
public class InfoPanel extends JPanel {

    public InfoPanel() {
        setLayout(null);
        setBackground(new Color(40, 40, 40));

        // LSB section
        JLabel lsbTitle = new JLabel("Least Significant Bit");
        lsbTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        lsbTitle.setForeground(Color.WHITE);
        lsbTitle.setBounds(40, 30, 300, 30);
        add(lsbTitle);

        JTextArea lsbText = buildTextArea(
            "LSB replaces the least significant bit of the pixel with the information to be hidden. " +
            "Since LSB is replaced there is no effect on cover image and hence unintended user will " +
            "not get the idea that some message is hidden behind the image. However there is a little " +
            "change in level of intensity of original and modified pixel, but it cannot be detected visually.\n\n" +
            "The following example explains how the letter 'A' can be hidden into the three pixels " +
            "i.e. eight bytes of a 24-bit image.\n\n" +
            " Pixels: (00100111 11101011 11001010)          A: 01000001\n" +
            "         (00100111 11011000 10101001)\n" +
            "         (11001000 00110111 11011001)\n\n" +
            "Result:  (00100110 11101011 11001010)\n" +
            "         (00100111 11011000 10101000)\n" +
            "         (11001001 00110111 11011001)"
        );
        lsbText.setBounds(70, 60, 820, 280);
        add(lsbText);

        // PVD section
        JLabel pvdTitle = new JLabel("Pixel Value Differencing");
        pvdTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        pvdTitle.setForeground(Color.WHITE);
        pvdTitle.setBounds(40, 360, 300, 30);
        add(pvdTitle);

        JTextArea pvdText = buildTextArea(
            "The pixel-value differencing (PVD) scheme uses the difference value between two consecutive " +
            "pixels in a block to determine how many secret bits should be embedded.\n\n" +
            "In the process of embedding a secret message, a cover image is partitioned into " +
            "non-overlapping blocks of two consecutive pixels. A difference value is calculated from " +
            "the values of the two pixels in each block. All possible difference values are classified " +
            "into a number of ranges. The selection of the range intervals is based on the characteristics " +
            "of human vision's sensitivity to gray value variations from smoothness to contrast.\n\n" +
            "The difference value then is replaced by a new value to embed the value of a sub-stream of " +
            "the secret message. The number of bits which can be embedded in a pixel pair is decided by " +
            "the width of the range that the difference value belongs to.\n\n" +
            "This method provides an easy way to produce a more imperceptible result than those yielded " +
            "by simple least-significant-bit replacement methods."
        );
        pvdText.setBounds(70, 390, 820, 220);
        add(pvdText);

        addBackgroundImage();
    }

    private JTextArea buildTextArea(String content) {
        JTextArea area = new JTextArea(content);
        area.setBackground(new Color(102, 102, 102, 0)); // transparent
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        area.setForeground(Color.WHITE);
        area.setBorder(null);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        return area;
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
