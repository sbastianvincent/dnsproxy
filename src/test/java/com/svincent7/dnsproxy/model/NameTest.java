package com.svincent7.dnsproxy.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NameTest {
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
    void testConstructor() {
        MessageInput messageInput = new MessageInput(DATA);
        messageInput.setPosition(12);

        Name name = new Name(messageInput);

        Assertions.assertEquals("www.example.com.", name.getName());
        Assertions.assertEquals(17, name.getLength());

        MessageOutput messageOutput = new MessageOutput(0);
        name.toByteResponse(messageOutput);
        Assertions.assertEquals(3, messageOutput.getData()[0]); // www
        Assertions.assertEquals(7, messageOutput.getData()[4]); // example
        Assertions.assertEquals(3, messageOutput.getData()[12]); // com
    }

    @Test
    void testEmptyName() {
        Name emptyName = new Name("");
        Assertions.assertEquals("", emptyName.getName());
        Assertions.assertEquals(1, emptyName.getLength());

        MessageOutput emptyMessageOutput = new MessageOutput(0);
        emptyName.toByteResponse(emptyMessageOutput);
        Assertions.assertEquals(0, emptyMessageOutput.getData()[0]);
    }

    @Test
    void testGetLength_EmptyLabelsIgnored() {
        Name emptyName = new Name("example..com");
        Assertions.assertEquals(13, emptyName.getLength());
    }

    @Test
    void testClone() {
        Name name = new Name("www.example.com.");
        Name clone = name.clone();

        Assertions.assertEquals("www.example.com.", clone.getName());
    }

    @Test
    public void testReadName_Compressed() {
        // Construct packet: [3]'w''w''w'[0xC0][0x04] — pointer to offset 4 ("example.com.")
        byte[] name = new byte[] {
                3, 'w', 'w', 'w',
                7, 'e', 'x', 'a', 'm', 'p', 'l', 'e',
                3, 'c', 'o', 'm',
                0,
                3, 'f', 'o', 'o',
                (byte) 0xC0, 0x00  // pointer to offset 0 => "www.example.com."
        };
        MessageInput input = new MessageInput(name);

        Name nameStr = new Name(input);

        Assertions.assertEquals("www.example.com.", nameStr.getName());
    }

    @Test
    public void testReadName_PointerToSelf_Throws() {
        byte[] name = new byte[] {
                (byte) 0xC0, 0x00 // pointer to self (offset 0)
        };
        MessageInput input = new MessageInput(name);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                new Name(input)
        );
        Assertions.assertTrue(ex.getMessage().contains("Invalid pointer to self"));
    }

    @Test
    public void testReadName_TooDeep_Throws() {
        final int depth = 20;
        byte[] name = new byte[depth * 2];
        for (int i = 0; i < depth; i++) {
            int offset = i * 2;
            name[offset] = (byte) 0xC0;
            name[offset + 1] = (byte) ((offset + 2) & 0xFF); // point to next
        }

        // Final pointer still points somewhere valid to terminate (won't be reached)
        name[name.length - 2] = 0x00;

        MessageInput input = new MessageInput(name);

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                new Name(input)
        );
        Assertions.assertTrue(ex.getMessage().contains("Too many compression pointers"));
    }
}
