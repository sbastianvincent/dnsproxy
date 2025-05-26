package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParameterPortTest {
    @Test
    void testConstructor() {
        ParameterPort port = new ParameterPort();

        Assertions.assertNotNull(port);
    }

    @Test
    void testClone() throws DNSMessageParseException {
        byte[] data = new byte[]{0x01, 0x02};

        ParameterPort port = new ParameterPort();
        port.fromByteArray(data);

        ParameterSvcBinding clone = port.clone();

        Assertions.assertEquals(port.getPort(), ((ParameterPort) clone).getPort());
        Assertions.assertArrayEquals(port.toByteArr(), clone.toByteArr());
        Assertions.assertArrayEquals(data, port.toByteArr());
    }
}
