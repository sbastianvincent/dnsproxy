package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterIpv4HintTest {

    @Test
    void testConstructor() {
        ParameterIpv4Hint hint = new ParameterIpv4Hint();

        Assertions.assertNotNull(hint);
        Assertions.assertNotNull(hint.getAddresses());
    }

    @Test
    void testClone() throws DNSMessageParseException {
        byte[] ipv4HintParam = new byte[] {
                (byte) 0xC0, (byte) 0xA8, 0x01, 0x01, // 192.168.1.1
                (byte) 0xC0, (byte) 0xA8, 0x01, 0x02  // 192.168.1.2
        };

        ParameterIpv4Hint hint = new ParameterIpv4Hint();
        hint.fromByteArray(ipv4HintParam);

        ParameterSvcBinding clone = hint.clone();

        Assertions.assertEquals(hint.getAddresses().size(), ((ParameterIpv4Hint) clone).getAddresses().size());
        Assertions.assertArrayEquals(ipv4HintParam, clone.toByteArr());
    }

    @Test
    void testFromByteArray_AnythingLeft() {
        byte[] ipv4HintParam = new byte[] {
                (byte) 0xC0, (byte) 0xA8, 0x01, 0x01, // 192.168.1.1
                (byte) 0xC0, (byte) 0xA8, 0x01, 0x02,  // 192.168.1.2
                0x01
        };

        ParameterIpv4Hint hint = new ParameterIpv4Hint();
        Assertions.assertThrows(DNSMessageParseException.class, () -> hint.fromByteArray(ipv4HintParam));
    }
}
