package com.svincent7.dnsproxy.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class CryptoUtils {

    private CryptoUtils() {

    }

    public static String sha256(final String str) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception exception) {
            throw new RuntimeException("Error while generating SHA-256 hash", exception);
        }
    }
}
