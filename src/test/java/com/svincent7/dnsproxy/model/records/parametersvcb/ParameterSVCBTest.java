package com.svincent7.dnsproxy.model.records.parametersvcb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterSVCBTest {

    @Test
    void testFromValue() {
        ParameterSVCB parameterSVCB = ParameterSVCB.fromValue(0);
        Assertions.assertEquals(0, parameterSVCB.getValue());
    }

    @Test
    void testFromValueNull_ReturnUnknown() {
        ParameterSVCB parameterSVCB = ParameterSVCB.fromValue(999);
        Assertions.assertEquals(ParameterSVCB.UNKNOWN, parameterSVCB);
    }
}
