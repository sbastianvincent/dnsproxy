package com.svincent7.dnsproxy.model.records.parametersvcb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterUnknownTest {

    @Test
    void testConstructor() {
        ParameterUnknown parameterUnknown = new ParameterUnknown(10);

        Assertions.assertNotNull(parameterUnknown);
        Assertions.assertEquals(10, parameterUnknown.getKey());
        Assertions.assertNull(parameterUnknown.getValue());
    }

    @Test
    void testClone() {
        byte[] data = new byte[20];
        ParameterUnknown parameterUnknown = new ParameterUnknown(10);
        parameterUnknown.fromByteArray(data);

        ParameterSvcBinding clone = parameterUnknown.clone();

        Assertions.assertEquals(parameterUnknown.getKey(), ((ParameterUnknown)clone).getKey());
        Assertions.assertArrayEquals(data, parameterUnknown.getValue());
        Assertions.assertArrayEquals(parameterUnknown.getValue(), ((ParameterUnknown)clone).getValue());
        Assertions.assertArrayEquals(data, parameterUnknown.toByteArr());
        Assertions.assertArrayEquals(parameterUnknown.toByteArr(), clone.toByteArr());
    }
}
