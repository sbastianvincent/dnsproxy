package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CNameRecordTest {

    @Test
    void testConstructor() {
        Name name = Mockito.mock(Name.class);
        Type type = Mockito.mock(Type.class);
        DNSClass dnsClass = Mockito.mock(DNSClass.class);
        long ttl = 20L;
        int length = 16;
        MessageInput messageInput = Mockito.mock(MessageInput.class);

        Name singleName = new Name(messageInput);

        CNAMERecord cnameRecord = new CNAMERecord(name, type, dnsClass, ttl, length, messageInput);

        Assertions.assertEquals(name, cnameRecord.getName());
        Assertions.assertEquals(type, cnameRecord.getType());
        Assertions.assertEquals(dnsClass, cnameRecord.getDnsClass());
        Assertions.assertEquals(ttl, cnameRecord.getTtl());
        Assertions.assertEquals(length, cnameRecord.getLength());
        Assertions.assertEquals(singleName.getName(), cnameRecord.getSingleName().getName());
    }

    @Test
    void testConstructor_Cname() {
        long ttl = 20L;

        CNAMERecord cnameRecord = new CNAMERecord("domainName", ttl, new Name("cname"));

        Assertions.assertEquals("domainName", cnameRecord.getName().getName());
        Assertions.assertEquals(Type.CNAME, cnameRecord.getType());
        Assertions.assertEquals(DNSClass.IN, cnameRecord.getDnsClass());
        Assertions.assertEquals(ttl, cnameRecord.getTtl());
        Assertions.assertEquals(7, cnameRecord.getLength());
        Assertions.assertEquals("cname", cnameRecord.getSingleName().getName());
    }

    @Test
    void testClone() {
        Name name = new Name("CNAMERecord");
        Type type = Mockito.mock(Type.class);
        DNSClass dnsClass = Mockito.mock(DNSClass.class);
        long ttl = 20L;
        int length = 16;
        MessageInput messageInput = Mockito.mock(MessageInput.class);

        Name singleName = new Name(messageInput);

        CNAMERecord cnameRecord = new CNAMERecord(name, type, dnsClass, ttl, length, messageInput);
        Record clone = cnameRecord.clone();

        Assertions.assertEquals(name.getName(), clone.getName().getName());
        Assertions.assertEquals(type, clone.getType());
        Assertions.assertEquals(dnsClass, clone.getDnsClass());
        Assertions.assertEquals(ttl, clone.getTtl());
        Assertions.assertEquals(length, clone.getLength());
        Assertions.assertEquals(singleName.getName(), ((CNAMERecord) clone).getSingleName().getName());
    }

    @Test
    void testRRToByteResponse() {
        CNAMERecord cnameRecord = new CNAMERecord("domainName", 20, new Name("cname"));
        MessageOutput messageOutput = new MessageOutput();

        cnameRecord.rrToByteResponse(messageOutput);

        Assertions.assertNotNull(messageOutput.getData());

    }
}
