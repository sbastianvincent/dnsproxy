package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@Slf4j
public class HTTPSRecordTest {

    @Test
    public void testHTTPSRecordWithValidParameters() throws Exception {
        byte[] httpsResponse = new byte[] {
                // DNS Header
                0x00, 0x01,             // ID
                (byte)0x81, (byte)0x80, // Flags: Standard response, recursion available
                0x00, 0x01,             // QDCOUNT = 1
                0x00, 0x01,             // ANCOUNT = 1
                0x00, 0x00,             // NSCOUNT = 0
                0x00, 0x00,             // ARCOUNT = 0

                // Question: svc.example.com. IN HTTPS
                0x03, 's', 'v', 'c',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,
                0x00, 0x41,             // QTYPE = 65 (HTTPS)
                0x00, 0x01,             // QCLASS = IN

                // Answer section
                (byte)0xC0, 0x0C,       // NAME (pointer to offset 12, "svc.example.com.")
                0x00, 0x41,             // TYPE = 65 (HTTPS)
                0x00, 0x01,             // CLASS = IN
                0x00, 0x00, 0x0E, 0x10, // TTL = 3600
                0x00, 0x15,             // RDLENGTH = 21 bytes

                // RDATA (HTTPS record data)
                0x00, 0x00,             // Priority = 0
                0x00,                   // root label (empty target name)

                // Parameter: key=0 (mandatory), length=2, value=0x0001 (mandatory ALPN)
                0x00, 0x00,             // key 0 (MANDATORY)
                0x00, 0x02,             // length 2
                0x00, 0x01,             // value (mandatory key = 1 for ALPN)

                // Parameter: key=1 (alpn), length=3, value=0x02 'h' '2'
                0x00, 0x01,             // key 1 (ALPN)
                0x00, 0x03,             // length 3
                0x02, 'h', '2'          // ALPN = "h2"
        };

        MessageInput input = new MessageInput(httpsResponse);
        // Skip DNS header (12 bytes)
        input.setPosition(12);

        new Name(input);

        input.readU16(); // QTYPE
        input.readU16(); // QCLASS

        Name answerName = new Name(input);

        Type type = Type.fromValue(input.readU16());
        DNSClass dnsClass = DNSClass.fromValue(input.readU16());
        long ttl = input.readU32();
        int rdLength = input.readU16();

        HTTPSRecord httpsRecord = new HTTPSRecord(answerName, type, dnsClass, ttl, rdLength, input);

        Assertions.assertEquals(0, httpsRecord.getSvcPriority());
        Assertions.assertEquals("", httpsRecord.getTargetName().getName());
        Assertions.assertTrue(httpsRecord.getSvcParams().containsKey(0));
        Assertions.assertTrue(httpsRecord.getSvcParams().containsKey(1));
    }

    @Test
    public void testHTTPSRecord_UnexpectedMessageRemaining() throws Exception {
        byte[] httpsResponse = new byte[] {
                // DNS Header
                0x00, 0x01,             // ID
                (byte)0x81, (byte)0x80, // Flags: Standard response, recursion available
                0x00, 0x01,             // QDCOUNT = 1
                0x00, 0x01,             // ANCOUNT = 1
                0x00, 0x00,             // NSCOUNT = 0
                0x00, 0x00,             // ARCOUNT = 0

                // Question: svc.example.com. IN HTTPS
                0x03, 's', 'v', 'c',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,
                0x00, 0x41,             // QTYPE = 65 (HTTPS)
                0x00, 0x01,             // QCLASS = IN

                // Answer section
                (byte)0xC0, 0x0C,       // NAME (pointer to offset 12, "svc.example.com.")
                0x00, 0x41,             // TYPE = 65 (HTTPS)
                0x00, 0x01,             // CLASS = IN
                0x00, 0x00, 0x0E, 0x10, // TTL = 3600
                0x00, 0x15,             // RDLENGTH = 21 bytes

                // RDATA (HTTPS record data)
                0x00, 0x00,             // Priority = 0
                0x00,                   // root label (empty target name)

                // Parameter: key=0 (mandatory), length=2, value=0x0001 (mandatory ALPN)
                0x00, 0x00,             // key 0 (MANDATORY)
                0x00, 0x02,             // length 2
                0x00, 0x01,             // value (mandatory key = 1 for ALPN)

                // Parameter: key=1 (alpn), length=3, value=0x02 'h' '2'
                0x00, 0x01,             // key 1 (ALPN)
                0x00, 0x03,             // length 3
                0x02, 'h', '2',          // ALPN = "h2"
                0x01
        };

        MessageInput input = new MessageInput(httpsResponse);
        // Skip DNS header (12 bytes)
        input.setPosition(12);

        new Name(input);

        input.readU16(); // QTYPE
        input.readU16(); // QCLASS

        Name answerName = new Name(input);

        Type type = Type.fromValue(input.readU16());
        DNSClass dnsClass = DNSClass.fromValue(input.readU16());
        long ttl = input.readU32();
        int rdLength = input.readU16();

        Assertions.assertThrows(DNSMessageParseException.class, () -> new HTTPSRecord(answerName, type, dnsClass, ttl, rdLength, input));
    }

    @Test
    public void testHTTPSRecord_NoMandatoryParam() throws Exception {
        byte[] httpsResponse = new byte[] {
                // DNS Header
                0x00, 0x01,             // ID
                (byte)0x81, (byte)0x80, // Flags: Standard response, recursion available
                0x00, 0x01,             // QDCOUNT = 1
                0x00, 0x01,             // ANCOUNT = 1
                0x00, 0x00,             // NSCOUNT = 0
                0x00, 0x00,             // ARCOUNT = 0

                // Question: svc.example.com. IN HTTPS
                0x03, 's', 'v', 'c',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,
                0x00, 0x41,             // QTYPE = 65 (HTTPS)
                0x00, 0x01,             // QCLASS = IN

                // Answer section
                (byte)0xC0, 0x0C,       // NAME (pointer to offset 12, "svc.example.com.")
                0x00, 0x41,             // TYPE = 65 (HTTPS)
                0x00, 0x01,             // CLASS = IN
                0x00, 0x00, 0x0E, 0x10, // TTL = 3600
                0x00, 0x01,             // RDLENGTH = 21 bytes

                // RDATA (HTTPS record data)
                0x00, 0x00,             // Priority = 0
                0x00,                   // root label (empty target name)

                // Parameter: key=0 (mandatory), length=2, value=0x0001 (mandatory ALPN)
                0x00, 0x00,             // key 0 (MANDATORY)
                0x00, 0x02,             // length 2
                0x00, 0x01,             // value (mandatory key = 1 for ALPN)
        };

        MessageInput input = new MessageInput(httpsResponse);
        // Skip DNS header (12 bytes)
        input.setPosition(12);

        new Name(input);

        input.readU16(); // QTYPE
        input.readU16(); // QCLASS

        Name answerName = new Name(input);

        Type type = Type.fromValue(input.readU16());
        DNSClass dnsClass = DNSClass.fromValue(input.readU16());
        long ttl = input.readU32();
        int rdLength = input.readU16();

        Assertions.assertThrows(DNSMessageParseException.class, () -> new HTTPSRecord(answerName, type, dnsClass, ttl, rdLength, input));
    }

    @Test
    void testClone() throws DNSMessageParseException {
        byte[] httpsResponse = new byte[] {
                // DNS Header
                0x00, 0x01,             // ID
                (byte)0x81, (byte)0x80, // Flags: Standard response, recursion available
                0x00, 0x01,             // QDCOUNT = 1
                0x00, 0x01,             // ANCOUNT = 1
                0x00, 0x00,             // NSCOUNT = 0
                0x00, 0x00,             // ARCOUNT = 0

                // Question: svc.example.com. IN HTTPS
                0x03, 's', 'v', 'c',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,
                0x00, 0x41,             // QTYPE = 65 (HTTPS)
                0x00, 0x01,             // QCLASS = IN

                // Answer section
                (byte)0xC0, 0x0C,       // NAME (pointer to offset 12, "svc.example.com.")
                0x00, 0x41,             // TYPE = 65 (HTTPS)
                0x00, 0x01,             // CLASS = IN
                0x00, 0x00, 0x0E, 0x10, // TTL = 3600
                0x00, 0x15,             // RDLENGTH = 21 bytes

                // RDATA (HTTPS record data)
                0x00, 0x00,             // Priority = 0
                0x00,                   // root label (empty target name)

                // Parameter: key=0 (mandatory), length=2, value=0x0001 (mandatory ALPN)
                0x00, 0x00,             // key 0 (MANDATORY)
                0x00, 0x02,             // length 2
                0x00, 0x01,             // value (mandatory key = 1 for ALPN)

                // Parameter: key=1 (alpn), length=3, value=0x02 'h' '2'
                0x00, 0x01,             // key 1 (ALPN)
                0x00, 0x03,             // length 3
                0x02, 'h', '2'          // ALPN = "h2"
        };

        MessageInput input = new MessageInput(httpsResponse);
        // Skip DNS header (12 bytes)
        input.setPosition(12);

        new Name(input);

        input.readU16(); // QTYPE
        input.readU16(); // QCLASS

        Name answerName = new Name(input);

        Type type = Type.fromValue(input.readU16());
        DNSClass dnsClass = DNSClass.fromValue(input.readU16());
        long ttl = input.readU32();
        int rdLength = input.readU16();

        HTTPSRecord httpsRecord = new HTTPSRecord(answerName, type, dnsClass, ttl, rdLength, input);
        Record clone = httpsRecord.clone();

        Assertions.assertEquals(0, ((HTTPSRecord) clone).getSvcPriority());
        Assertions.assertEquals("", ((HTTPSRecord) clone).getTargetName().getName());
        Assertions.assertTrue(((HTTPSRecord) clone).getSvcParams().containsKey(0));
        Assertions.assertTrue(((HTTPSRecord) clone).getSvcParams().containsKey(1));
    }

    @Test
    void testRRToByResponse() throws DNSMessageParseException {
        MessageOutput messageOutput = Mockito.mock(MessageOutput.class);
        byte[] httpsResponse = new byte[] {
                // DNS Header
                0x00, 0x01,             // ID
                (byte)0x81, (byte)0x80, // Flags: Standard response, recursion available
                0x00, 0x01,             // QDCOUNT = 1
                0x00, 0x01,             // ANCOUNT = 1
                0x00, 0x00,             // NSCOUNT = 0
                0x00, 0x00,             // ARCOUNT = 0

                // Question: svc.example.com. IN HTTPS
                0x03, 's', 'v', 'c',
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                0x03, 'c', 'o', 'm',
                0x00,
                0x00, 0x41,             // QTYPE = 65 (HTTPS)
                0x00, 0x01,             // QCLASS = IN

                // Answer section
                (byte)0xC0, 0x0C,       // NAME (pointer to offset 12, "svc.example.com.")
                0x00, 0x41,             // TYPE = 65 (HTTPS)
                0x00, 0x01,             // CLASS = IN
                0x00, 0x00, 0x0E, 0x10, // TTL = 3600
                0x00, 0x15,             // RDLENGTH = 21 bytes

                // RDATA (HTTPS record data)
                0x00, 0x00,             // Priority = 0
                0x00,                   // root label (empty target name)

                // Parameter: key=0 (mandatory), length=2, value=0x0001 (mandatory ALPN)
                0x00, 0x00,             // key 0 (MANDATORY)
                0x00, 0x02,             // length 2
                0x00, 0x01,             // value (mandatory key = 1 for ALPN)

                // Parameter: key=1 (alpn), length=3, value=0x02 'h' '2'
                0x00, 0x01,             // key 1 (ALPN)
                0x00, 0x03,             // length 3
                0x02, 'h', '2'          // ALPN = "h2"
        };

        MessageInput input = new MessageInput(httpsResponse);
        // Skip DNS header (12 bytes)
        input.setPosition(12);

        new Name(input);

        input.readU16(); // QTYPE
        input.readU16(); // QCLASS

        Name answerName = new Name(input);

        Type type = Type.fromValue(input.readU16());
        DNSClass dnsClass = DNSClass.fromValue(input.readU16());
        long ttl = input.readU32();
        int rdLength = input.readU16();

        // Step 5: Construct HTTPSRecord with actual answerName and RDATA slice
        HTTPSRecord httpsRecord = new HTTPSRecord(answerName, type, dnsClass, ttl, rdLength, input);

        httpsRecord.rrToByteResponse(messageOutput);

        Assertions.assertNotNull(messageOutput);
    }
}
