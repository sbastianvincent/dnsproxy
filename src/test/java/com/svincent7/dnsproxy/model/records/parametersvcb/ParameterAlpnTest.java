package com.svincent7.dnsproxy.model.records.parametersvcb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterAlpnTest {
    static final byte[] alpnParam = new byte[] {
            // Each ALPN protocol is length-prefixed
            0x02, 'h', '2',                  // "h2"
            0x08, 'h', 't', 't', 'p', '/', '1', '.', '1' // "http/1.1"
    };

    @Test
    void testConstructor() {
        ParameterAlpn alpn = new ParameterAlpn();

        Assertions.assertNotNull(alpn);
        Assertions.assertNotNull(alpn.getValues());
    }

    @Test
    void testClone() {
        ParameterAlpn alpn = new ParameterAlpn();
        alpn.fromByteArray(alpnParam);

        ParameterAlpn clone = alpn.clone();

        Assertions.assertNotSame(alpn, clone);
        Assertions.assertEquals(alpn.getValues().size(), clone.getValues().size());
    }

    @Test
    void testToByteArray() {
        ParameterAlpn alpn = new ParameterAlpn();
        alpn.fromByteArray(alpnParam);

        byte[] arr = alpn.toByteArr();
        Assertions.assertArrayEquals(alpnParam, arr);
    }
}
