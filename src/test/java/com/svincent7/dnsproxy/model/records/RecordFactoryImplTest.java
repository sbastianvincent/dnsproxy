package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.MessageInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RecordFactoryImplTest {

    private RecordFactoryImpl factory;

    @BeforeEach
    public void setUp() {
        factory = new RecordFactoryImpl();
    }

    @Test
    void testQRecord() throws DNSMessageParseException {
        byte[] data = new byte[]{
                0x03, 'w', 'w', 'w',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,
                0x00, 0x01,             // QTYPE = 1
                0x00, 0x01,             // QCLASS = IN
        };
        MessageInput input = new MessageInput(data);
        Record record = factory.getRecordFromDnsMessage(input, 0);

        Assertions.assertNotNull(record);
        Assertions.assertTrue(record instanceof QRecord);
    }

    @Test
    void testARecord() throws DNSMessageParseException {
        byte[] data = new byte[]{
                0x03, 'w', 'w', 'w',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,
                0x00, 0x01,             // QTYPE = 1
                0x00, 0x01,             // QCLASS = IN
                0x00, 0x00, 0x00, 0x3c, // TTL 60 seconds
                0x00, 0x04,             // RDLENGTH = 4 bytes
                (byte) 0x5d, (byte) 0xb8, (byte) 0xd8, 0x22 // RDATA = 93.184.216.34
        };
        MessageInput input = new MessageInput(data);
        Record record = factory.getRecordFromDnsMessage(input, 1);

        Assertions.assertNotNull(record);
        Assertions.assertTrue(record instanceof ARecord);
    }

    @Test
    void testSoaRecord() throws DNSMessageParseException {
        byte[] soaRecord = new byte[] {
                // NAME: example.com.
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,

                // TYPE = SOA (6)
                0x00, 0x06,

                // CLASS = IN (1)
                0x00, 0x01,

                // TTL = 3600 seconds
                0x00, 0x00, 0x0E, 0x10,

                // RDLENGTH = calculated below (41 bytes)
                0x00, 0x29,

                // MNAME: ns1.example.com.
                0x03, 'n', 's', '1',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,

                // RNAME: admin.example.com.
                0x05, 'a', 'd', 'm', 'i', 'n',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,

                // SERIAL: 2025052501
                (byte)0x78, (byte)0xD9, (byte)0xC3, (byte)0x35,

                // REFRESH: 7200
                0x00, 0x00, 0x1C, 0x20,

                // RETRY: 3600
                0x00, 0x00, 0x0E, 0x10,

                // EXPIRE: 1209600
                0x00, 0x12, (byte)0x75, 0x00,

                // MINIMUM: 86400
                0x00, 0x01, 0x51, (byte) 0x80
        };

        MessageInput input = new MessageInput(soaRecord);
        Record record = factory.getRecordFromDnsMessage(input, 1);

        Assertions.assertNotNull(record);
        Assertions.assertTrue(record instanceof SOARecord);
    }

    @Test
    void testAAAARecord() throws DNSMessageParseException {
        byte[] aaaaRecord = new byte[] {
            // NAME: www.example.com.
            0x03, 'w', 'w', 'w',
            0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
            0x03, 'c', 'o', 'm',
            0x00,

            // TYPE = AAAA (28)
            0x00, 0x1C,

            // CLASS = IN (1)
            0x00, 0x01,

            // TTL = 60 seconds
            0x00, 0x00, 0x00, 0x3C,

            // RDLENGTH = 16 bytes
            0x00, 0x10,

            // RDATA = 2001:0db8:85a3:0000:0000:8a2e:0370:7334
            (byte)0x20, 0x01,
            0x0d, (byte)0xb8,
            (byte)0x85, (byte)0xa3,
            0x00, 0x00,
            0x00, 0x00,
            (byte)0x8a, 0x2e,
            0x03, 0x70,
            0x73, 0x34
        };


        MessageInput input = new MessageInput(aaaaRecord);
        Record record = factory.getRecordFromDnsMessage(input, 1);

        Assertions.assertNotNull(record);
        Assertions.assertTrue(record instanceof AAAARecord);
    }

    @Test
    void testCNameRecord() throws DNSMessageParseException {
        byte[] cnameRecord = new byte[] {
            // NAME: www.example.com.
            0x03, 'w', 'w', 'w',
            0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
            0x03, 'c', 'o', 'm',
            0x00,

            // TYPE = CNAME (5)
            0x00, 0x05,

            // CLASS = IN (1)
            0x00, 0x01,

            // TTL = 60 seconds
            0x00, 0x00, 0x00, 0x3C,

            // RDLENGTH = 13 bytes ("example.com." name)
            0x00, 0x0D,

            // RDATA = example.com.
            0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
            0x03, 'c', 'o', 'm',
            0x00
        };

        MessageInput input = new MessageInput(cnameRecord);
        Record record = factory.getRecordFromDnsMessage(input, 1);

        Assertions.assertNotNull(record);
        Assertions.assertTrue(record instanceof CNAMERecord);
    }

    @Test
    void testHTTPSRecord() throws DNSMessageParseException {
        byte[] httpsRecord = new byte[] {
            // NAME: svc.example.com.
            0x03, 's', 'v', 'c',
            0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
            0x03, 'c', 'o', 'm',
            0x00,

            // TYPE = HTTPS (65)
            0x00, 0x41,

            // CLASS = IN (1)
            0x00, 0x01,

            // TTL = 60 seconds
            0x00, 0x00, 0x00, 0x3C,

            // RDLENGTH = 21 bytes
            0x00, 0x15,

            // Priority = 0
            0x00, 0x00,

            // TargetName = root label (empty name)
            0x00,

            // PARAM 0: MANDATORY = [1]
            0x00, 0x00,             // Key = 0 (MANDATORY)
            0x00, 0x02,             // Length = 2
            0x00, 0x01,             // Value = 1 (ALPN required)

            // PARAM 1: ALPN = "h2"
            0x00, 0x01,             // Key = 1 (ALPN)
            0x00, 0x03,             // Length = 3
            0x02, 'h', '2'          // ALPN value = "h2"
        };


        MessageInput input = new MessageInput(httpsRecord);
        Record record = factory.getRecordFromDnsMessage(input, 1);

        Assertions.assertNotNull(record);
        Assertions.assertTrue(record instanceof HTTPSRecord);
    }

    @Test
    void testUnknownRecord() throws DNSMessageParseException {
        byte[] unknown = new byte[] {
            // NAME: svc.example.com.
            0x03, 'w', 'w', 'w',
            0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
            0x03, 'c', 'o', 'm',
            0x00,

            // TYPE = NS (2) -> not implemented yet
            0x00, 0x02,

            // CLASS = IN (1)
            0x00, 0x01,

            // TTL = 60 seconds
            0x00, 0x00, 0x00, 0x3C,

            // RDLENGTH = 1 bytes
            0x00, 0x01
        };


        MessageInput input = new MessageInput(unknown);
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.getRecordFromDnsMessage(input, 1));
    }
}
