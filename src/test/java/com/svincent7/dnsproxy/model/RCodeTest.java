package com.svincent7.dnsproxy.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RCodeTest {

    @Test
    void testFromValue() {
        RCode rCode = RCode.fromValue(0);
        Assertions.assertEquals(0, rCode.getValue());
    }

    @Test
    void testFromValue_ThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RCode.fromValue(999));
    }
}
