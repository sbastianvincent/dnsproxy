package com.svincent7.dnsproxy.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DNSClassTest {

    @Test
    void testFromValue() {
        DNSClass val = DNSClass.fromValue(1);
        Assertions.assertEquals(1, val.getValue());
    }

    @Test
    void testFromValue_ThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DNSClass.fromValue(999));
    }
}
