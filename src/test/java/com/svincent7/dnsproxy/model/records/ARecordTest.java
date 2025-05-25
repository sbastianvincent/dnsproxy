package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ARecordTest {

    @Test
    void testConstructor() throws UnknownHostException {
        Name name = Mockito.mock(Name.class);
        Type type = Mockito.mock(Type.class);
        DNSClass dnsClass = Mockito.mock(DNSClass.class);
        Long ttl = 20L;
        Integer length = 16;
        MessageInput messageInput = Mockito.mock(MessageInput.class);

        byte[] ipv4Bytes = InetAddress.getByName("192.168.0.1").getAddress();
        Mockito.when(messageInput.readByteArray(4)).thenReturn(ipv4Bytes);

        int ipAddress = ARecord.ipToInt("192.168.0.1");

        ARecord aRecord = new ARecord(name, type, dnsClass, ttl, length, messageInput);

        Assertions.assertEquals(name, aRecord.getName());
        Assertions.assertEquals(type, aRecord.getType());
        Assertions.assertEquals(dnsClass, aRecord.getDnsClass());
        Assertions.assertEquals(ttl, aRecord.getTtl());
        Assertions.assertEquals(length, aRecord.getLength());
        Assertions.assertEquals(ipAddress, aRecord.getAddr());
        Assertions.assertEquals("192.168.0.1", aRecord.getIpAddress());
    }

    @Test
    void testConstructor_DomainName() throws UnknownHostException {
        Long ttl = 20L;
        String ipAddress = "192.168.0.1";

        ARecord aRecord = new ARecord("example.com", ttl, ipAddress);

        Assertions.assertEquals("example.com", aRecord.getName().getName());
        Assertions.assertEquals(Type.A, aRecord.getType());
        Assertions.assertEquals(DNSClass.IN, aRecord.getDnsClass());
        Assertions.assertEquals(ttl, aRecord.getTtl());
        Assertions.assertEquals(4, aRecord.getLength());
        Assertions.assertEquals(ARecord.ipToInt("192.168.0.1"), aRecord.getAddr());
        Assertions.assertEquals("192.168.0.1", aRecord.getIpAddress());
    }

    @Test
    void testClone() {
        long ttl = 20L;
        String ipAddress = "192.168.0.1";

        ARecord aRecord = new ARecord("example.com", ttl, ipAddress);
        Record clone = aRecord.clone();

        Assertions.assertNotSame(aRecord, clone);

        Assertions.assertEquals(aRecord.getName().getName(), clone.getName().getName());
        Assertions.assertEquals(aRecord.getType(), clone.getType());
        Assertions.assertEquals(aRecord.getDnsClass(), clone.getDnsClass());
        Assertions.assertEquals(aRecord.getTtl(), clone.getTtl());
        Assertions.assertEquals(aRecord.getLength(), clone.getLength());
        Assertions.assertEquals(aRecord.getAddr(), ((ARecord) clone).getAddr());
        Assertions.assertEquals(aRecord.getIpAddress(), ((ARecord) clone).getIpAddress());
    }

    @Test
    void testIpToInt() {
        int ipAddressInt = ARecord.ipToInt("192.168.0.1");

        Assertions.assertEquals(-1062731775, ipAddressInt);
    }

    @Test
    void testIpToInt_ThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ARecord.ipToInt("192.168.0"));
    }
}
