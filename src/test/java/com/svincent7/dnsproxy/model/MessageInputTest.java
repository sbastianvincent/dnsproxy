package com.svincent7.dnsproxy.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.ByteBuffer;

public class MessageInputTest {

    @Test
    void testConstructor() {
        MessageInput messageInput = new MessageInput(new byte[]{});
        MessageInput messageInput2 = new MessageInput(Mockito.mock(ByteBuffer.class));

        Assertions.assertNotNull(messageInput);
        Assertions.assertNotNull(messageInput2);
    }

    @Test
    void testRead() {
        byte[] inputBytes = new byte[]{
                0x00,                   // readU8() = 0x00
                0x01, 0x02,             // readU16() = 0x0102
                0x03, 0x04, 0x05, 0x06, // readU32() = 0x03040506
                0x07, 0x08, 0x09,       // readByteArray(3)
                0x0a,                   // getByteAt(10)
                0x05, 0x61, 0x62, 0x63, 0x64, 0x65 // readCountedString() = "abcde"
        };

        MessageInput messageInput = new MessageInput(inputBytes);

        Assertions.assertEquals(0x00, messageInput.readU8());
        Assertions.assertEquals(0x0102, messageInput.readU16());
        Assertions.assertEquals(0x03040506, messageInput.readU32());

        byte[] arr = messageInput.readByteArray(3);
        Assertions.assertEquals(0x07, arr[0]);
        Assertions.assertEquals(0x08, arr[1]);
        Assertions.assertEquals(0x09, arr[2]);

        Assertions.assertEquals(10, messageInput.getPosition());
        Assertions.assertEquals((byte) 0x0a, messageInput.getByteAt(10));
        Assertions.assertEquals(7, messageInput.remaining());

        messageInput.setPosition(11);
        byte[] countedStr = messageInput.readCountedString();
        Assertions.assertEquals(5, countedStr.length);
        Assertions.assertArrayEquals(new byte[]{'a', 'b', 'c', 'd', 'e'}, countedStr);
    }
}
