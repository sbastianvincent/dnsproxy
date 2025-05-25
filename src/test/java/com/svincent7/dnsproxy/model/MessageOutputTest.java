package com.svincent7.dnsproxy.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageOutputTest {
    @Test
    void testConstructor() {
        MessageOutput messageOutput = new MessageOutput(10);

        MessageOutput messageOutput2 = new MessageOutput();

        Assertions.assertNotNull(messageOutput);
        Assertions.assertEquals(10, messageOutput.getData().length);
        Assertions.assertEquals(0, messageOutput.getPos());

        Assertions.assertNotNull(messageOutput2);
        Assertions.assertEquals(32, messageOutput2.getData().length);
        Assertions.assertEquals(0, messageOutput2.getPos());
    }

    @Test
    void testWrite() {
        MessageOutput messageOutput = new MessageOutput();

        messageOutput.writeU8(0x00);
        messageOutput.writeU16(0x0102);

        Assertions.assertEquals(0x00, messageOutput.getData()[0]);
        Assertions.assertEquals(0x01, messageOutput.getData()[1]);
        Assertions.assertEquals(0x02, messageOutput.getData()[2]);

        messageOutput.writeU16At(0x0304, 0);
        Assertions.assertEquals(0x03, messageOutput.getData()[0]);
        Assertions.assertEquals(0x04, messageOutput.getData()[1]);

        Assertions.assertEquals(3, messageOutput.getPos());

        messageOutput.writeU32(0x05060708);
        Assertions.assertEquals(0x05, messageOutput.getData()[3]);
        Assertions.assertEquals(0x06, messageOutput.getData()[4]);
        Assertions.assertEquals(0x07, messageOutput.getData()[5]);
        Assertions.assertEquals(0x08, messageOutput.getData()[6]);

        messageOutput.writeByteArray(new byte[]{0x09, 0x0a});
        Assertions.assertEquals(0x09, messageOutput.getData()[7]);
        Assertions.assertEquals(0x0a, messageOutput.getData()[8]);

        messageOutput.writeByteArray(new byte[]{0x09, 0x0a}, 1, 1);
        Assertions.assertEquals(0x0a, messageOutput.getData()[9]);

        messageOutput.writeCountedString(new byte[] {'a', 'b'});
        Assertions.assertEquals(2, messageOutput.getData()[10]);
        Assertions.assertEquals('a', messageOutput.getData()[11]);
        Assertions.assertEquals('b', messageOutput.getData()[12]);

        Assertions.assertArrayEquals(new byte[]{0x03, 0x04, 0x02, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0a, 2, 'a', 'b'}, messageOutput.toByteArray());
    }

    @Test
    void testWriteU16_PastEndOfData() {
        MessageOutput messageOutput = new MessageOutput();
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageOutput.writeU16At(0x00, 0));
    }

    @Test
    void testWriteCountedString_OutOfLength() {
        MessageOutput messageOutput = new MessageOutput();
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageOutput.writeCountedString(new byte[256]));
    }

    @Test
    void testEnsureCapacityBelowPos() {
        MessageOutput messageOutput = new MessageOutput(8);
        messageOutput.setPos(7);
        messageOutput.writeByteArray(new byte[10]);

        Assertions.assertEquals(17, messageOutput.getPos());

        byte[] result = messageOutput.toByteArray();
        Assertions.assertEquals(17, result.length);
    }
}
