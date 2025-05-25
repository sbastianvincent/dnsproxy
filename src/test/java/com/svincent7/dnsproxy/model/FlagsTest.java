package com.svincent7.dnsproxy.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlagsTest {

    @Test
    void testFromValue() {
        Flags flags = Flags.fromValue(0);
        Assertions.assertEquals(0, flags.getValue());
    }

    @Test
    void testFromValue_ThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Flags.fromValue(999));
    }
}
