package com.svincent7.dnsproxy.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CryptoUtilsTest {

    @Test
    public void testSha256Hashing() {
        // Arrange
        String input = "hello world";
        String expectedHash = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9";

        // Act
        String actualHash = CryptoUtils.sha256(input);

        // Assert
        Assertions.assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testSha256EmptyString() {
        // Arrange
        String input = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        // Act
        String actualHash = CryptoUtils.sha256(input);

        // Assert
        Assertions.assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testSha256NullInputThrowsException() {
        // Arrange
        String input = null;

        // Act & Assert
        Assertions.assertThrows(RuntimeException.class, () -> CryptoUtils.sha256(input));
    }
}
