package com.svincent7.dnsproxy.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeTest {

    @Test
    void testFromValue() {
        Type type = Type.fromValue(1);
        Assertions.assertEquals(1, type.getValue());
    }

    @Test
    void testFromValue_ThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Type.fromValue(999));
    }
}
