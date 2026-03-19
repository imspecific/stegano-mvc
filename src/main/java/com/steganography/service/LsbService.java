package com.steganography.service;

import com.steganography.model.SteganographyResult;
import com.steganography.util.ImageSteganographyUtil;

import java.awt.image.BufferedImage;

/**
 * --- How LSB works ---
 * Each character is split into four 2-bit groups.
 * Each group is embedded into the two least-significant bits of one pixel's
 * raw RGB integer. So one character needs 4 pixels.
 *
 * Example — hiding 'A' (ASCII 65 = 01000001) in two pixels:
 *   Original pixel:  00100111 11101011 11001010 (raw int)
 *   After embed bit: 00100110 11101011 11001010 (LSB replaced with 01)
 *
 * The payload is wrapped in "!encoded!<length>!" before encoding so the
 * decoder can verify the image is encoded and know how many chars to read.
 */
public class LsbService {

    /**
     * Encodes a message into a copy of the cover image using 2-bit LSB substitution.
     *
     * Original location: EncDecButtonActionPerformed (ende == 1)
     * Also duplicated in: EncDecButton1ActionPerformed (ende == 1)
     *
     * Core original loop:
     *   pixOut = (pixel & 0xFFFFFFFC) | twoBitMessage[count++];
     *   steganoImg.setRGB(i, j, pixOut);
     *
     * @param originalImg  the cover image (not modified)
     * @param message      the plaintext to hide
     * @return SteganographyResult with the stego image, or a failure result
     */
    public SteganographyResult encode(BufferedImage originalImg, String message) {
        // Wrap the message: "!encoded!" + charCount + "!" + message
        String framed    = ImageSteganographyUtil.SENTINEL + message.length() + "!" + message;
        int    msgLength = framed.length();

        // Check if the image is large enough (4 pixels per character)
        int pixelsNeeded = 4 * msgLength;
        int pixelsAvail  = originalImg.getWidth() * originalImg.getHeight();
        if (pixelsNeeded > pixelsAvail) {
            return SteganographyResult.failure(
                "Image too small! Need " + pixelsNeeded + " pixels, image has " + pixelsAvail);
        }

        // Split each character into four 2-bit groups
        // Original: twoBitMessage[4*i+0] = (currentChar >> 6) & 0x3; etc.
        int[] twoBitChunks = new int[4 * msgLength];
        for (int i = 0; i < msgLength; i++) {
            char c = framed.charAt(i);
            twoBitChunks[4 * i]     = (c >> 6) & 0x3;
            twoBitChunks[4 * i + 1] = (c >> 4) & 0x3;
            twoBitChunks[4 * i + 2] = (c >> 2) & 0x3;
            twoBitChunks[4 * i + 3] =  c        & 0x3;
        }

        // Write into a fresh copy so the original stays unchanged
        BufferedImage stegoImg = deepCopy(originalImg);
        int count = 0;

        outer:
        for (int col = 0; col < originalImg.getWidth(); col++) {
            for (int row = 0; row < originalImg.getHeight(); row++) {
                if (count >= 4 * msgLength) break outer;

                int pixel  = originalImg.getRGB(col, row);
                int pixOut = (pixel & 0xFFFFFFFC) | twoBitChunks[count++]; // replace 2 LSBs
                stegoImg.setRGB(col, row, pixOut);
            }
        }

        return SteganographyResult.encodeSuccess(stegoImg);
    }

    /**
     * Decodes a message from a stego image encoded with LSB.
     *
     * Original location: EncDecButtonActionPerformed (ende == 2)
     * Also duplicated in: EncDecButton1ActionPerformed (ende == 2)
     *
     * Core original extraction:
     *   temp = pixel & 0x03;
     *   listChar.add(temp);
     *   if (listChar.size() >= 4) {
     *       charOut = (listChar.pop() << 6) | ... | listChar.pop();
     *       decodedMsg.append((char) charOut);
     *   }
     *
     * @param stegoImg the image that may contain a hidden message
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
    // Helper
    // -----------------------------------------------------------------------

    /** Creates a pixel-perfect copy of an image so encoding never mutates the original. */
    private BufferedImage deepCopy(BufferedImage src) {
        BufferedImage copy = new BufferedImage(
            src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        copy.getGraphics().drawImage(src, 0, 0, null);
        return copy;
    }
}
