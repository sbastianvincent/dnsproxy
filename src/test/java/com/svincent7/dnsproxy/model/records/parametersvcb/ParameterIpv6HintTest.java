package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterIpv6HintTest {

    @Test
    void testConstructor() {
        ParameterIpv6Hint hint = new ParameterIpv6Hint();

        Assertions.assertNotNull(hint);
        Assertions.assertNotNull(hint.getAddresses());
    }

    @Test
    void testClone() throws DNSMessageParseException {
        byte[] ipv6HintParam = new byte[] {
                // 2001:0db8:85a3:0000:0000:8a2e:0370:7334
                (byte)0x20, (byte)0x01, (byte)0x0d, (byte)0xb8,
                (byte)0x85, (byte)0xa3, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x8a, (byte)0x2e,
                (byte)0x03, (byte)0x70, (byte)0x73, (byte)0x34,

                // 2001:0db8:85a3:0000:0000:8a2e:0370:7335
                (byte)0x20, (byte)0x01, (byte)0x0d, (byte)0xb8,
                (byte)0x85, (byte)0xa3, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x8a, (byte)0x2e,
                (byte)0x03, (byte)0x70, (byte)0x73, (byte)0x35
        };

        ParameterIpv6Hint hint = new ParameterIpv6Hint();
        hint.fromByteArray(ipv6HintParam);

        ParameterSvcBinding clone = hint.clone();

        Assertions.assertEquals(hint.getAddresses().size(), ((ParameterIpv6Hint) clone).getAddresses().size());
        Assertions.assertArrayEquals(ipv6HintParam, clone.toByteArr());
    }
}
