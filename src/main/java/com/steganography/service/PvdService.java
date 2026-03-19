package com.steganography.service;

import com.steganography.model.SteganographyResult;
import com.steganography.util.ImageSteganographyUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Pixel Value Differencing (PVD) steganography — encode and decode.
 *
 * In the original Home.java the PVD encode algorithm was inside
 * EndecButtonActionPerformed (ende == 1), and the decode was
 * EndecButtonActionPerformed (ende == 2).
 * Two helper methods also lived on the Home class:
 *   int findDifference(Color c1, Color c2)
 *   int checkRange(int a)
 *
 * All of that is now cleanly isolated here.
 *
 * --- How PVD works ---
 * Instead of blindly replacing the 2 LSBs of every pixel (LSB), PVD looks
 * at pairs of adjacent pixels.  The difference between them tells us how many
 * bits we can safely embed: a smooth area (small difference) is sensitive to
 * change so we embed fewer bits; an edge area (large difference) can absorb
 * more change without the human eye noticing.
 *
 * Range table (from the original range[] and value[] arrays):
 *   range[] = {0, 8, 16, 32, 64, 128}   — band lower-bounds
 *   value[] = {0xFFFFFFFE, ..., 0xFFFFFF80} — masks to clear N LSBs
 *
 * A difference in band 0 (0–7)   → 0 bits embeddable (flat area, don't touch)
 * A difference in band 1 (8–15)  → up to 3 bits
 * A difference in band 2 (16–31) → up to 4 bits
 * etc.
 */
public class PvdService {

    // -----------------------------------------------------------------------
    // Original constants from Home.java
    // -----------------------------------------------------------------------

    /** Range band lower-bounds.  Original: int range[] = {0,8,16,32,64,128}; */
    private static final int[] RANGE = {0, 8, 16, 32, 64, 128};

    /**
     * Bitmasks that clear the N least-significant bits of a pixel value.
     * Original: int value[] = {0xFFFFFFFE, 0xFFFFFFFC, 0xFFFFFFF8, ...};
     * Used to zero out bits before writing a message chunk into them.
     */
    private static final int[] VALUE = {
        0xFFFFFFFE, 0xFFFFFFFC, 0xFFFFFFF8,
        0xFFFFFFF0, 0xFFFFFFE0, 0xFFFFFFC0, 0xFFFFFF80
    };

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Encodes a message into a copy of the cover image using PVD.
     *
     * Original location: EndecButtonActionPerformed (ende == 1)
     *
     * The original loop:
     *   d  = findDifference(c1, c2);
     *   m  = (int)(Math.log(d) / Math.log(2.0));
     *   for (x = 0; x < m; x++) pixOut = (pixel & value[x]) | oneBitMessage[count];
     *   d1 = findDifference(new Color(pixOut), c2);
     *   if (checkRange(d) == checkRange(d1)) { steganoImg.setRGB(i, j, pixOut); }
     *
     * @param originalImg the cover image (not modified)
     * @param message     the plaintext to hide
     * @return SteganographyResult with the stego image
     */
    public SteganographyResult encode(BufferedImage originalImg, String message) {
        // Wrap: "!encoded!" + length + "!" + message
        String framed    = ImageSteganographyUtil.SENTINEL + message.length() + "!" + message;
        int    msgLength = framed.length();

        // Split each character into 8 individual bits
        // Original: oneBitMessage[8*i+0] = (currentChar >> 7) & 0x1; etc.
        int[] oneBitChunks = new int[8 * msgLength];
        for (int i = 0; i < msgLength; i++) {
            char c = framed.charAt(i);
            for (int b = 0; b < 8; b++) {
                oneBitChunks[8 * i + b] = (c >> (7 - b)) & 0x1;
            }
        }

        BufferedImage stegoImg = deepCopy(originalImg);
        int count = 0, tempCount = 0;

        outer:
        for (int col = 0; col < originalImg.getWidth(); col++) {
            for (int row = 0; row < originalImg.getHeight(); row++) {
                if (row >= originalImg.getHeight() - 1) continue; // need a pair

                Color c1 = new Color(originalImg.getRGB(col, row));
                Color c2 = new Color(originalImg.getRGB(col, row + 1));

                int d = findDifference(c1, c2);
                int m = (d == 0) ? 0 : (int) (Math.log(d) / Math.log(2.0));

                // Probe: compute what the pixel would look like after embedding
                int pixOut = originalImg.getRGB(col, row);
                for (int x = 0; x < m && tempCount < oneBitChunks.length; x++) {
                    pixOut = (originalImg.getRGB(col, row) & VALUE[x]) | oneBitChunks[tempCount];
                    tempCount++;
                }

                int d1 = findDifference(new Color(pixOut), c2);

                // Guard against log(0) in checkRange (original code did the same)
                if (d  == 0 || d  == 1) d  = 2;
                if (d1 == 0 || d1 == 1) d1 = 2;

                // Only embed if the difference stays in the same range band
                // (preserves the image's natural smoothness-to-edge gradient)
                if (checkRange(d) == checkRange(d1)) {
                    for (int x = 0; x < m; x++) {
                        if (count >= 8 * msgLength) break outer;
                        int pixel = originalImg.getRGB(col, row);
                        int px    = (pixel & 0xFFFFFFFE) | oneBitChunks[count++];
                        stegoImg.setRGB(col, row, px);
                    }
                }
            }
        }

        return SteganographyResult.encodeSuccess(stegoImg);
    }

    /**
     * Decodes a message from a PVD stego image.
     *
     * Original location: EndecButtonActionPerformed (ende == 2)
     *
     * Note: the original PVD decode branch reused the same LSB 2-bit extraction
     * loop (pixel & 0x03) rather than inverting the PVD embedding step.
     * That behaviour is preserved here via ImageSteganographyUtil.extractPayload().
     *
     * @param stegoImg the image containing the hidden message
     * @return SteganographyResult with the decoded text, or failure if not encoded
     */
    public SteganographyResult decode(BufferedImage stegoImg) {
        if (!ImageSteganographyUtil.isEncoded(stegoImg)) {
            return SteganographyResult.failure("This image does not contain a hidden message.");
        }

        int    msgLength = ImageSteganographyUtil.getEncodedLength(stegoImg);
        String text      = ImageSteganographyUtil.extractPayload(stegoImg, msgLength);

        return SteganographyResult.decodeSuccess(text);
    }

    // -----------------------------------------------------------------------
    // Private helpers — exact originals from Home.java, just moved here
    // -----------------------------------------------------------------------

    /**
     * Computes the average absolute colour difference between two adjacent pixels.
     *
     * Original method: int findDifference(Color c1, Color c2)
     *   return (int) Math.abs(((r1-r2) + (g1-g2) + (b1-b2)) / 3);
     */
    private int findDifference(Color c1, Color c2) {
        int dr = c1.getRed()   - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue()  - c2.getBlue();
        return (int) Math.abs((dr + dg + db) / 3.0);
    }

    /**
     * Maps a pixel-pair difference to a range band index.
     *
     * Original method: int checkRange(int a)
     *   for (int i = 0; i < range.length; i++)
     *       if (a >= range[i] && a < Math.pow(2, i+3)) val = i;
     *   return val;
     */
    private int checkRange(int diff) {
        int band = 0;
        for (int i = 0; i < RANGE.length; i++) {
            if (diff >= RANGE[i] && diff < Math.pow(2, i + 3)) {
                band = i;
            }
        }
        return band;
    }

    /** Creates a pixel-perfect copy so encoding never mutates the original. */
    private BufferedImage deepCopy(BufferedImage src) {
        BufferedImage copy = new BufferedImage(
            src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        copy.getGraphics().drawImage(src, 0, 0, null);
        return copy;
    }
}
