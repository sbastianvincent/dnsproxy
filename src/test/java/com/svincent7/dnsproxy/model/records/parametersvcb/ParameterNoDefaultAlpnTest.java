package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterNoDefaultAlpnTest {

    @Test
    void testConstructor() {
        ParameterNoDefaultAlpn parameterNoDefaultAlpn = new ParameterNoDefaultAlpn();

        Assertions.assertNotNull(parameterNoDefaultAlpn);
    }

    @Test
    void testClone() throws DNSMessageParseException {
        byte[] data = new byte[] {};

        ParameterNoDefaultAlpn parameterNoDefaultAlpn = new ParameterNoDefaultAlpn();
        parameterNoDefaultAlpn.fromByteArray(data);

        ParameterSvcBinding clone = parameterNoDefaultAlpn.clone();

        Assertions.assertArrayEquals(parameterNoDefaultAlpn.toByteArr(), clone.toByteArr());
        Assertions.assertArrayEquals(new byte[0], clone.toByteArr());
    }

    @Test
    void testFromByteArr_NotEmpty_ThrowException() throws DNSMessageParseException {
        ParameterNoDefaultAlpn parameterNoDefaultAlpn = new ParameterNoDefaultAlpn();

        Assertions.assertThrows(DNSMessageParseException.class, () -> parameterNoDefaultAlpn.fromByteArray(new byte[] {0x01, 0x02}));
    }
}
