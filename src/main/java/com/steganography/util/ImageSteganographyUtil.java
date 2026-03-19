package com.steganography.util;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Shared low-level helpers for reading steganography payloads from images.
 *
 * In the original Home.java these were three instance methods scattered among
 * the Swing event handlers:
 *
 *   boolean isEncoded(BufferedImage, int width, int height)
 *   int     getEncodedLength(BufferedImage, int width, int height)
 *
 * Both are used by LSB decode AND PVD decode, so they lived as helpers on the
 * Home class. Now they live here as static utilities, shared by both service classes.
 *
 * Message framing convention (invented in the original code, preserved here):
 *   Every hidden payload is prepended with:  "!encoded!" + charCount + "!" + actualMessage
 *   e.g. hiding "Hi" becomes:               "!encoded!2!Hi"
 *
 *   The decoder first checks for "!encoded!" to confirm the image has a payload,
 *   then reads the digit(s) before the second "!" to know how many chars to extract.
 */
public class ImageSteganographyUtil {

    /** The sentinel string prepended to every hidden message. */
    public static final String SENTINEL = "!encoded!";

    /**
     * Checks whether the image contains an LSB-encoded message by reading
     * the first 9 characters and comparing them to "!encoded!".
     *
     * Original termination: count == 45
     * (9 chars × 4 pixels/char = 36 pixel reads, but the original counter
     *  also incremented inside the char-assembly block, giving 45.)
     *
     * @param image the image to inspect
     * @return true if the sentinel is found
     */
    public static boolean isEncoded(BufferedImage image) {
        StringBuilder decoded = new StringBuilder();
        Deque<Integer> bits   = new ArrayDeque<>();

        int count = 0;
        outer:
        for (int col = 0; col < image.getWidth(); col++) {
            for (int row = 0; row < image.getHeight(); row++, count++) {
                if (count == 45) break outer;

                int pixel = image.getRGB(col, row);
                int lsb2  = pixel & 0x03; // extract two LSBs
                bits.add(lsb2);

                if (bits.size() >= 4) {
                    int ch = (bits.pop() << 6) | (bits.pop() << 4)
                           | (bits.pop() << 2) |  bits.pop();
                    decoded.append((char) ch);
                    count++; // original double-increments here
                }
            }
        }

        return SENTINEL.equals(decoded.toString());
    }

    /**
     * Reads the hidden character count from the message header.
     *
     * After the 9-character sentinel ("!encoded!"), the original code stored
     * the payload length as a decimal string terminated by '!'.
     * This method skips the 36-pixel sentinel block, then reads characters
     * until it hits the closing '!'.
     *
     * @param image the stego image (must pass isEncoded first)
     * @return number of payload characters hidden in the image
     */
    public static int getEncodedLength(BufferedImage image) {
        StringBuilder lengthStr = new StringBuilder();
        Deque<Integer> bits     = new ArrayDeque<>();

        int pixelCount = 0;
        outer:
        for (int col = 0; col < image.getWidth(); col++) {
            for (int row = 0; row < image.getHeight(); row++) {
                if (pixelCount < 36) {     // skip 36 pixels = 9 sentinel characters
                    pixelCount++;
                    continue;
                }

                int pixel = image.getRGB(col, row);
                int lsb2  = pixel & 0x03;
                bits.add(lsb2);

                if (bits.size() >= 4) {
                    int ch = (bits.pop() << 6) | (bits.pop() << 4)
                           | (bits.pop() << 2) |  bits.pop();
                    if ((char) ch == '!') break outer; // closing delimiter
                    lengthStr.append((char) ch);
                }
            }
        }

        return Integer.parseInt(lengthStr.toString());
    }

    /**
     * Extracts exactly msgLength characters of payload from a stego image,
     * skipping both the sentinel and the length header.
     *
     * This extraction loop appeared copy-pasted in three different event handlers
     * in the original code (LSB decode, PVD-LSB decode, PVD decode).
     * It is now written once here.
     *
     * @param image     the stego image
     * @param msgLength the number of characters to extract (from getEncodedLength)
     * @return the extracted plaintext
     */
    public static String extractPayload(BufferedImage image, int msgLength) {
        StringBuilder decoded = new StringBuilder();
        Deque<Integer> bits   = new ArrayDeque<>();

        // pixels to skip = 36 (sentinel) + 4 × (digits in length number + 1 for closing '!')
        int skipPixels = 36 + 4 * (String.valueOf(msgLength).length() + 1);
        int skipped = 0, extracted = 0;

        outer:
        for (int col = 0; col < image.getWidth(); col++) {
            for (int row = 0; row < image.getHeight(); row++) {
                if (skipped < skipPixels) { skipped++; continue; }
                if (extracted++ == 4 * msgLength) break outer;

                int pixel = image.getRGB(col, row);
                int lsb2  = pixel & 0x03;
                bits.add(lsb2);

                if (bits.size() >= 4) {
                    int ch = (bits.pop() << 6) | (bits.pop() << 4)
                           | (bits.pop() << 2) |  bits.pop();
                    decoded.append((char) ch);
                }
            }
        }

        return decoded.toString();
    }
}
