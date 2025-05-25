package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SOARecordTest {

    @Test
    public void testConstructorWithMessageInput() {
        byte[] rawData = new byte[] {
                // host: www.example.com.
                3, 'w', 'w', 'w',
                7, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                3, 'c', 'o', 'm',
                0,

                // admin: foo.www.example.com. (label + pointer to offset 0)
                3, 'f', 'o', 'o',
                (byte) 0xC0, 0x00,  // pointer to "www.example.com." at offset 0

                // serial
                0x00, 0x00, 0x00, 0x01,
                // refresh
                0x00, 0x00, 0x0E, 0x10,
                // retry
                0x00, 0x00, 0x01, 0x2C,
                // expire
                0x00, 0x01, 0x51, (byte) 0x80,
                // minimum
                0x00, 0x00, 0x01, 0x2C
        };
        MessageInput messageInput = new MessageInput(rawData);

        Name name = new Name("example.com.");
        long ttl = 3600;
        int length = 0;

        SOARecord soa = new SOARecord(name, Type.SOA, DNSClass.IN, ttl, length, messageInput);

        Assertions.assertEquals("example.com.", soa.getName().getName());
        Assertions.assertEquals(ttl, soa.getTtl());
        Assertions.assertEquals(length, soa.getLength());
        Assertions.assertEquals("www.example.com.", soa.getHost().getName());
        Assertions.assertEquals("foo.www.example.com.", soa.getAdmin().getName());
        Assertions.assertEquals(1L, soa.getSerial());
        Assertions.assertEquals(3600L, soa.getRefresh());
        Assertions.assertEquals(300L, soa.getRetry());
        Assertions.assertEquals(86400L, soa.getExpire());
        Assertions.assertEquals(300L, soa.getMinimum());
    }

    @Test
    void testClone() {
        byte[] rawData = new byte[] {
                // host: www.example.com.
                3, 'w', 'w', 'w',
                7, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                3, 'c', 'o', 'm',
                0,

                // admin: foo.www.example.com. (label + pointer to offset 0)
                3, 'f', 'o', 'o',
                (byte) 0xC0, 0x00,  // pointer to "www.example.com." at offset 0

                // serial
                0x00, 0x00, 0x00, 0x01,
                // refresh
                0x00, 0x00, 0x0E, 0x10,
                // retry
                0x00, 0x00, 0x01, 0x2C,
                // expire
                0x00, 0x01, 0x51, (byte) 0x80,
                // minimum
                0x00, 0x00, 0x01, 0x2C
        };
        MessageInput messageInput = new MessageInput(rawData);

        Name name = new Name("example.com.");
        long ttl = 3600;
        int length = 0;

        SOARecord soa = new SOARecord(name, Type.SOA, DNSClass.IN, ttl, length, messageInput);
        Record clone = soa.clone();

        Assertions.assertEquals("example.com.", clone.getName().getName());
        Assertions.assertEquals(ttl, clone.getTtl());
        Assertions.assertEquals(length, clone.getLength());
        Assertions.assertEquals("www.example.com.", ((SOARecord) clone).getHost().getName());
        Assertions.assertEquals("foo.www.example.com.", ((SOARecord) clone).getAdmin().getName());
        Assertions.assertEquals(1L, ((SOARecord) clone).getSerial());
        Assertions.assertEquals(3600L, ((SOARecord) clone).getRefresh());
        Assertions.assertEquals(300L, ((SOARecord) clone).getRetry());
        Assertions.assertEquals(86400L, ((SOARecord) clone).getExpire());
        Assertions.assertEquals(300L, ((SOARecord) clone).getMinimum());
    }

    @Test
    void testRRToByteResponse() {
        byte[] rawData = new byte[] {
                // host: www.example.com.
                3, 'w', 'w', 'w',
                7, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                3, 'c', 'o', 'm',
                0,

                // admin: foo.www.example.com. (label + pointer to offset 0)
                3, 'f', 'o', 'o',
                (byte) 0xC0, 0x00,  // pointer to "www.example.com." at offset 0

                // serial
                0x00, 0x00, 0x00, 0x01,
                // refresh
                0x00, 0x00, 0x0E, 0x10,
                // retry
                0x00, 0x00, 0x01, 0x2C,
                // expire
                0x00, 0x01, 0x51, (byte) 0x80,
                // minimum
                0x00, 0x00, 0x01, 0x2C
        };
        MessageInput messageInput = new MessageInput(rawData);

        Name name = new Name("example.com.");
        long ttl = 3600;
        int length = 0;

        SOARecord soa = new SOARecord(name, Type.SOA, DNSClass.IN, ttl, length, messageInput);
        MessageOutput messageOutput = new MessageOutput();

        soa.rrToByteResponse(messageOutput);

        Assertions.assertNotNull(messageOutput);
    }
}
