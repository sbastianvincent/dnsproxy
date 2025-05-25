package com.svincent7.dnsproxy.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HeaderTest {

    @Test
    void testConstructorFromMessageInput() {
        MessageInput input = Mockito.mock(MessageInput.class);
        Mockito.when(input.readU16()).thenReturn(100).thenReturn(50)
                .thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(4);

        Header header = new Header(input);

        Assertions.assertEquals(100, header.getTransactionId());
        Assertions.assertEquals(50, header.getFlags());
        Assertions.assertEquals(1, header.getCounts()[0]);
        Assertions.assertEquals(2, header.getCounts()[1]);
        Assertions.assertEquals(3, header.getCounts()[2]);
        Assertions.assertEquals(4, header.getCounts()[3]);
    }

    @Test
    void testConstructor() {
        int[] counts = new int[] {1, 2, 3, 4};

        Header header = new Header(100, 50, counts);

        Assertions.assertEquals(100, header.getTransactionId());
        Assertions.assertEquals(50, header.getFlags());
        Assertions.assertEquals(1, header.getCounts()[0]);
        Assertions.assertEquals(2, header.getCounts()[1]);
        Assertions.assertEquals(3, header.getCounts()[2]);
        Assertions.assertEquals(4, header.getCounts()[3]);
    }

    @Test
    void testRCode() {
        Header header = new Header(100, 50, new int[] {1, 2, 3, 4});

        header.setRCode(RCode.NXDOMAIN);

        Assertions.assertEquals(RCode.NXDOMAIN, header.getRCode());
    }

    @Test
    void testFlag() {
        Header header = new Header(100, 50, new int[] {1, 2, 3, 4});

        header.setFlag(Flags.AA);

        Assertions.assertTrue(header.isFlagSet(Flags.AA));
        Assertions.assertFalse(header.isFlagSet(Flags.TC));
    }

    @Test
    void testTruncated() {
        Header header = new Header(100, 50, new int[] {1, 2, 3, 4});

        header.setFlag(Flags.TC);

        Assertions.assertTrue(header.isTruncated());
    }

    @Test
    void testNxDomain() {
        Header header = new Header(100, 50, new int[] {1, 2, 3, 4});

        header.setNxDomain();

        Assertions.assertEquals(RCode.NXDOMAIN, header.getRCode());
    }

    @Test
    void testToByteResponse() {
        Header header = new Header(100, 50, new int[] {1, 2, 3, 4});

        MessageOutput output = new MessageOutput();
        header.toByteResponse(output);

        Assertions.assertEquals(12, output.getPos());
    }

    @Test
    void testIncrementCount() {
        Header header = new Header(100, 50, new int[] {1, 2, 3, 4});

        header.incrementCount(1);

        Assertions.assertEquals(1, header.getCounts()[0]);
        Assertions.assertEquals(3, header.getCounts()[1]);
        Assertions.assertEquals(3, header.getCounts()[2]);
        Assertions.assertEquals(4, header.getCounts()[3]);
    }
}
