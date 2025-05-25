package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class QRecordTest {

    @Test
    void testConstructor() {
        Name name = Mockito.mock(Name.class);
        Type type = Mockito.mock(Type.class);
        DNSClass dnsClass = Mockito.mock(DNSClass.class);

        QRecord qRecord = new QRecord(name, type, dnsClass);

        Assertions.assertEquals(name, qRecord.getName());
        Assertions.assertEquals(type, qRecord.getType());
        Assertions.assertEquals(dnsClass, qRecord.getDnsClass());
    }

    @Test
    void testClone() {
        Name name = new Name("example.com");
        Type type = Mockito.mock(Type.class);
        DNSClass dnsClass = Mockito.mock(DNSClass.class);

        QRecord qRecord = new QRecord(name, type, dnsClass);
        Record clone = qRecord.clone();

        Assertions.assertEquals(name.getName(), clone.getName().getName());
        Assertions.assertEquals(qRecord.getType(), clone.getType());
        Assertions.assertEquals(qRecord.getType(), clone.getType());
        Assertions.assertEquals(qRecord.getDnsClass(), clone.getDnsClass());
    }

    @Test
    public void testRrToByteResponse_NoOp() {
        // Arrange
        Name name = new Name("example.com.");
        QRecord qRecord = new QRecord(name, Type.A, DNSClass.IN);
        MessageOutput messageOutput = new MessageOutput();

        int before = messageOutput.getPos();

        // Act
        qRecord.rrToByteResponse(messageOutput);

        // Assert
        int after = messageOutput.getPos();
        Assertions.assertEquals(before, after, "rrToByteResponse should not modify the MessageOutput");
    }
}
