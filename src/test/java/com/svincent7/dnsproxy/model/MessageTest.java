package com.svincent7.dnsproxy.model;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.records.ARecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageTest {
    private static final byte[] DATA = new byte[]{
            // Header (12 bytes)
            (byte) 0xAB, (byte) 0xCD,             // ID
            (byte) 0x01, (byte) 0x00,             // Flags
            0x00, 0x01,                           // QDCOUNT = 1
            0x00, 0x01,                           // ANCOUNT = 1
            0x00, 0x00,                           // NSCOUNT = 0
            0x00, 0x00,                           // ARCOUNT = 0

            // Question Section
            0x03, 'w', 'w', 'w',                  // www
            0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e', // example
            0x03, 'c', 'o', 'm',                  // com
            0x00,                                 // end of QNAME
            0x00, 0x01,                           // QTYPE = A
            0x00, 0x01,                           // QCLASS = IN

            // Answer Section
            (byte) 0xC0, 0x0C,                    // NAME = pointer to offset 0x0C
            0x00, 0x01,                           // TYPE = A
            0x00, 0x01,                           // CLASS = IN
            0x00, 0x00, 0x00, 0x3C,               // TTL = 60 seconds
            0x00, 0x04,                           // RDLENGTH = 4
            (byte) 0x5D, (byte) 0xB8, (byte) 0xD8, 0x22 // RDATA = 93.184.216.34
    };

    @Test
    void testConstructorMessageInput() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);

        Assertions.assertEquals(0xABCD, message.getHeader().getTransactionId());
        Assertions.assertEquals(0x0100, message.getHeader().getFlags());
        Assertions.assertEquals(0x0001, message.getHeader().getCounts()[0]);
        Assertions.assertEquals(0x0001, message.getHeader().getCounts()[1]);
        Assertions.assertEquals(0x0000, message.getHeader().getCounts()[2]);
        Assertions.assertEquals(0x0000, message.getHeader().getCounts()[3]);
        Assertions.assertNotNull(message.getSections());
        Assertions.assertEquals("www.example.com.", message.getSections().get(0).get(0).getName().getName());
        Assertions.assertEquals(Type.A, message.getSections().get(0).get(0).getType());
        Assertions.assertEquals(DNSClass.IN, message.getSections().get(0).get(0).getDnsClass());
        Assertions.assertEquals("www.example.com.", message.getSections().get(1).get(0).getName().getName());
        Assertions.assertEquals(Type.A, message.getSections().get(1).get(0).getType());
        Assertions.assertEquals(DNSClass.IN, message.getSections().get(1).get(0).getDnsClass());
        Assertions.assertEquals(60, message.getSections().get(1).get(0).getTtl());
        Assertions.assertEquals("93.184.216.34", ((ARecord) message.getSections().get(1).get(0)).getIpAddress());
    }

    @Test
    void testQueryComplete() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);

        Assertions.assertTrue(message.isQueryComplete());
        Assertions.assertFalse(message.isBlockedResponse());
    }

    @Test
    void testQueryNotComplete_Refused() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);

        message.getHeader().setRCode(RCode.REFUSED);

        Assertions.assertFalse(message.isQueryComplete());
        Assertions.assertTrue(message.isBlockedResponse());
    }

    @Test
    void testQueryNotComplete_NotAllQuestionAnswered() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);
        message.getSections().get(1).clear();

        Assertions.assertFalse(message.isQueryComplete());
    }

    @Test
    void testToByteResponse() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        MessageOutput messageOutput = new MessageOutput();
        Message message = new Message(input);

        message.toByteResponse(messageOutput, 512);

        Assertions.assertNotNull(messageOutput);
    }

    @Test
    void testGetQuestionRecords() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);

        Assertions.assertEquals("www.example.com.", message.getQuestionRecords().get(0).getName().getName());
    }

    @Test
    void testGetQuestionRecords_SectionsEmpty() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);
        message.getSections().clear();

        Assertions.assertTrue(message.getQuestionRecords().isEmpty());
    }

    @Test
    void testGetQuestionRecords_EmptySection() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);
        message.getSections().get(0).clear();

        Assertions.assertTrue(message.getQuestionRecords().isEmpty());
    }

    @Test
    void testAnswerRecords() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);

        message.addAnswerRecord(new ARecord(((ARecord) message.getAnswerRecords().get(0))));

        Assertions.assertEquals("www.example.com.", message.getAnswerRecords().get(0).getName().getName());
        Assertions.assertEquals("93.184.216.34", ((ARecord) message.getAnswerRecords().get(0)).getIpAddress());
        Assertions.assertEquals("93.184.216.34", ((ARecord) message.getAnswerRecords().get(1)).getIpAddress());
    }

    @Test
    void testAddAnswerRecord_EmptyAnswer() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);

        message.addAnswerRecord(new ARecord(((ARecord) message.getAnswerRecords().get(0))));

        Assertions.assertEquals("www.example.com.", message.getAnswerRecords().get(0).getName().getName());
        Assertions.assertEquals("93.184.216.34", ((ARecord) message.getAnswerRecords().get(0)).getIpAddress());
        Assertions.assertEquals("93.184.216.34", ((ARecord) message.getAnswerRecords().get(1)).getIpAddress());
    }

    @Test
    void testAddAnswerRecord_SectionsEmpty() throws DNSMessageParseException {
        byte[] data = new byte[]{
                // Header (12 bytes)
                (byte) 0xAB, (byte) 0xCD,             // ID
                (byte) 0x01, (byte) 0x00,             // Flags
                0x00, 0x00,                           // QDCOUNT = 0
                0x00, 0x00,                           // ANCOUNT = 0
                0x00, 0x00,                           // NSCOUNT = 0
                0x00, 0x00,                           // ARCOUNT = 0
        };
        MessageInput input = new MessageInput(data);
        Message message = new Message(input);

        message.addAnswerRecord(new ARecord("www.example.com.", 60, "93.184.216.34"));

        Assertions.assertEquals("www.example.com.", message.getAnswerRecords().get(0).getName().getName());
        Assertions.assertEquals("93.184.216.34", ((ARecord) message.getAnswerRecords().get(0)).getIpAddress());
    }

    @Test
    void testAnswerRecords_SectionsEmpty() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);
        message.getSections().clear();

        Assertions.assertTrue(message.getAnswerRecords().isEmpty());
    }

    @Test
    void testAnswerRecords_EmptySection() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);
        message.getSections().get(1).clear();

        Assertions.assertTrue(message.getAnswerRecords().isEmpty());
    }

    @Test
    void testSetBlocked() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        Message message = new Message(input);

        message.setBlocked();

        Assertions.assertTrue(message.isBlockedResponse());
    }

    @Test
    void testTruncated() throws DNSMessageParseException {
        MessageInput input = new MessageInput(DATA);
        MessageOutput messageOutput = new MessageOutput();
        Message message = new Message(input);

        message.toByteResponse(messageOutput, 16);

        Assertions.assertNotNull(messageOutput);
        Assertions.assertTrue(message.isTruncated());
    }

    @Test
    void testTruncated_AdditionalRR() throws DNSMessageParseException {
        byte[] data = new byte[]{
                // Header (12 bytes)
                (byte) 0xAB, (byte) 0xCD,             // ID
                (byte) 0x01, (byte) 0x00,             // Flags
                0x00, 0x01,                           // QDCOUNT = 1
                0x00, 0x01,                           // ANCOUNT = 1
                0x00, 0x00,                           // NSCOUNT = 0
                0x00, 0x02,                           // ARCOUNT = 0

                // Question Section
                0x03, 'w', 'w', 'w',                  // www
                0x07, 'e', 'x', 'a', 'm', 'p', 'l', 'e', // example
                0x03, 'c', 'o', 'm',                  // com
                0x00,                                 // end of QNAME
                0x00, 0x01,                           // QTYPE = A
                0x00, 0x01,                           // QCLASS = IN

                // Answer Section
                (byte) 0xC0, 0x0C,                    // NAME = pointer to offset 0x0C
                0x00, 0x01,                           // TYPE = A
                0x00, 0x01,                           // CLASS = IN
                0x00, 0x00, 0x00, 0x3C,               // TTL = 60 seconds
                0x00, 0x04,                           // RDLENGTH = 4
                (byte) 0x5D, (byte) 0xB8, (byte) 0xD8, 0x22, // RDATA = 93.184.216.34

                // AR1 Section
                (byte) 0xC0, 0x0C,                    // NAME = pointer to offset 0x0C
                0x00, 0x01,                           // TYPE = A
                0x00, 0x01,                           // CLASS = IN
                0x00, 0x00, 0x00, 0x3C,               // TTL = 60 seconds
                0x00, 0x04,                           // RDLENGTH = 4
                (byte) 0x5D, (byte) 0xB8, (byte) 0xD8, 0x22, // RDATA = 93.184.216.34

                // AR2 Section
                (byte) 0xC0, 0x0C,                    // NAME = pointer to offset 0x0C
                0x00, 0x01,                           // TYPE = A
                0x00, 0x01,                           // CLASS = IN
                0x00, 0x00, 0x00, 0x3C,               // TTL = 60 seconds
                0x00, 0x04,                           // RDLENGTH = 4
                (byte) 0x5D, (byte) 0xB8, (byte) 0xD8, 0x22 // RDATA = 93.184.216.34
        };
        MessageInput input = new MessageInput(data);
        MessageOutput messageOutput = new MessageOutput();
        Message message = new Message(input);

        message.toByteResponse(messageOutput, 16);

        Assertions.assertNotNull(messageOutput);
        Assertions.assertTrue(message.isTruncated());
        Assertions.assertEquals(0, message.getHeader().getCounts()[Header.SECTION_ADDITIONAL_RR]);
    }
}
