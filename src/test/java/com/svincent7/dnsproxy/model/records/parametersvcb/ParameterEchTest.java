package com.svincent7.dnsproxy.model.records.parametersvcb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterEchTest {

    @Test
    void testConstructor() {
        ParameterEch parameterEch = new ParameterEch();

        Assertions.assertNotNull(parameterEch);
    }

    @Test
    void testClone() {
        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04};
        ParameterEch parameterEch = new ParameterEch();
        parameterEch.fromByteArray(data);

        ParameterSvcBinding clone = parameterEch.clone();

        Assertions.assertArrayEquals(parameterEch.getData(), ((ParameterEch) clone).getData());
        Assertions.assertArrayEquals(parameterEch.toByteArr(), clone.toByteArr());
        Assertions.assertArrayEquals(data, clone.toByteArr());
    }
}
