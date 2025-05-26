package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterMandatoryTest {

    @Test
    void testConstructor() {
        ParameterMandatory pm = new ParameterMandatory();

        Assertions.assertNotNull(pm);
        Assertions.assertNotNull(pm.getValues());
    }

    @Test
    void testClone() throws DNSMessageParseException {
        byte[] mandatoryParam = new byte[] {
                0x00, 0x01, // key = 1 (ALPN)
                0x00, 0x03  // key = 3 (PORT)
        };

        ParameterMandatory pm = new ParameterMandatory();
        pm.fromByteArray(mandatoryParam);
        ParameterSvcBinding clone = pm.clone();

        Assertions.assertEquals(pm.getValues().size(), ((ParameterMandatory) clone).getValues().size());
        Assertions.assertArrayEquals(pm.toByteArr(), clone.toByteArr());
    }

    @Test
    void testFromByteArray_MessageLeft() throws DNSMessageParseException {
        byte[] mandatoryParam = new byte[] {
                0x00, 0x01, // key = 1 (ALPN)
                0x00, 0x03,  // key = 3 (PORT)
                0x04
        };

        ParameterMandatory pm = new ParameterMandatory();
        Assertions.assertThrows(DNSMessageParseException.class, () -> pm.fromByteArray(mandatoryParam));
    }
}
