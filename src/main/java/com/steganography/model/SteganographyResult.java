package com.steganography.model;

import java.awt.image.BufferedImage;

public class SteganographyResult {

    public enum Operation { ENCODE, DECODE }
    private final Operation operation;
    private final BufferedImage stegoImage;
    private final String decodedText;
    private final boolean success;
    private final String message;

    private SteganographyResult(Operation op, BufferedImage img, String text, boolean success, String message) {
        this.operation = op;
        this.stegoImage = img;
        this.decodedText = text;
        this.success = success;
        this.message = message;
    }

    public static SteganographyResult encodeSuccess(BufferedImage stegoImage) {
        return new SteganographyResult(Operation.ENCODE, stegoImage, null,true,"Encoding successful");
    }

    public static SteganographyResult decodeSuccess(String text) {
        return new SteganographyResult(Operation.DECODE, null, text,true,"Decoding successful");
    }

    public static SteganographyResult failure(String errorMessage) {
        return new SteganographyResult(null, null, null, false, errorMessage);
    }

    public Operation getOperation() { return operation; }
    public BufferedImage getStegoImage() { return stegoImage; }
    public String getDecodedText() { return decodedText; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
