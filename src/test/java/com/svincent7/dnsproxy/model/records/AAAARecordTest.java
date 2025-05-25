package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AAAARecordTest {

    @Test
    void testConstructor() throws UnknownHostException {
        Name name = Mockito.mock(Name.class);
        Type type = Mockito.mock(Type.class);
        DNSClass dnsClass = Mockito.mock(DNSClass.class);
        Long ttl = 20L;
        Integer length = 16;
        MessageInput messageInput = Mockito.mock(MessageInput.class);

        byte[] ipv6Bytes = InetAddress.getByName("2001:db8::1").getAddress();
        Mockito.when(messageInput.readByteArray(16)).thenReturn(ipv6Bytes);

        AAAARecord aaaaRecord = new AAAARecord(name, type, dnsClass, ttl, length, messageInput);

        Assertions.assertEquals(name, aaaaRecord.getName());
        Assertions.assertEquals(type, aaaaRecord.getType());
        Assertions.assertEquals(dnsClass, aaaaRecord.getDnsClass());
        Assertions.assertEquals(ttl, aaaaRecord.getTtl());
        Assertions.assertEquals(length, aaaaRecord.getLength());
        Assertions.assertEquals(ipv6Bytes, aaaaRecord.getAddress());
        Assertions.assertEquals("2001:db8:0:0:0:0:0:1", aaaaRecord.getIpAddress());
    }

    @Test
    void testConstructorWithoutMessageInput() throws UnknownHostException {
        String domain = "example.com";
        String ipv6 = "2001:db8:0:0:0:0:0:1";
        long ttl = 3600;

        AAAARecord record = new AAAARecord(domain, ttl, ipv6);

        Assertions.assertEquals(domain, record.getName().getName());
        Assertions.assertEquals("2001:db8:0:0:0:0:0:1", record.getIpAddress());
        Assertions.assertArrayEquals(InetAddress.getByName(ipv6).getAddress(), record.getAddress());
    }

    @Test
    void testGetIpInvalidAddress() {
        AAAARecord record = Mockito.mock(AAAARecord.class);
        Mockito.when(record.getName()).thenReturn(Mockito.mock(Name.class));
        Mockito.when(record.getType()).thenReturn(Mockito.mock(Type.class));
        Mockito.when(record.getDnsClass()).thenReturn(Mockito.mock(DNSClass.class));
        Mockito.when(record.getTtl()).thenReturn(60L);
        Mockito.when(record.getLength()).thenReturn(4);
        Mockito.when(record.getAddress()).thenReturn(new byte[]{});
        Mockito.when(record.getIpAddress()).thenReturn("invalid");

        AAAARecord clone = new AAAARecord(record);

        Assertions.assertNull(clone.getIpAddress());
    }

    @Test
    public void testIpv4MappedAddress() throws Exception {

        // ::ffff:192.0.2.128 in byte form
        byte[] ipv4MappedBytes = new byte[] {
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, (byte) 0xff, (byte) 0xff,
                (byte) 192, 0, 2, (byte) 128
        };

        MessageInput messageInput = Mockito.mock(MessageInput.class);
        Mockito.when(messageInput.readByteArray(16)).thenReturn(ipv4MappedBytes);

        Name name = new Name("example.com");
        long ttl = 3600;
        int length = 16;

        AAAARecord record = new AAAARecord(name, Type.AAAA, DNSClass.IN, ttl, length, messageInput);

        String expectedPrefix = "::ffff";
        String ipAddress = record.getIpAddress();

        Assertions.assertNotNull(ipAddress);
        Assertions.assertTrue(ipAddress.startsWith(expectedPrefix), "IP address should be prefixed with ::ffff");
        Assertions.assertTrue(ipAddress.endsWith("192.0.2.128"), "Should convert IPv4 part correctly");
    }

    @Test
    public void testIpv6ToBytesValid() throws Exception {
        String ipv6 = "2001:db8::1";
        byte[] bytes = AAAARecord.ipv6ToBytes(ipv6);
        Assertions.assertEquals(16, bytes.length);
        Assertions.assertArrayEquals(InetAddress.getByName(ipv6).getAddress(), bytes);
    }

    @Test
    public void testIpv6ToBytesInvalidLength() {
        String invalidIp = "::ffff:192.0.2.128";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AAAARecord.ipv6ToBytes(invalidIp);
        });
    }

    @Test
    public void testIpv6ToBytesInvalid() {
        String invalidIp = "invalid:ip";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AAAARecord.ipv6ToBytes(invalidIp);
        });
    }

    @Test
    public void testClone() throws Exception {
        AAAARecord original = new AAAARecord("example.com", 3600, "2001:db8:0:0:0:0:0:1");
        AAAARecord clone = (AAAARecord) original.clone();

        Assertions.assertNotSame(original, clone);
        Assertions.assertEquals(original.getName().getName(), clone.getName().getName());
        Assertions.assertEquals(original.getIpAddress(), clone.getIpAddress());
        Assertions.assertArrayEquals(original.getAddress(), clone.getAddress());
    }

    @Test
    void testRRToByteResponse() {
        String domain = "example.com";
        String ipv6 = "2001:db8:0:0:0:0:0:1";
        long ttl = 3600;

        MessageOutput output = new MessageOutput();
        AAAARecord record = new AAAARecord(domain, ttl, ipv6);

        record.rrToByteResponse(output);

        Assertions.assertNotNull(output);
    }

}
