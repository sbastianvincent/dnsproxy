package com.svincent7.dnsproxy.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DNSMessageParseExceptionTest {

    @Test
    void testDNSMessageParseException() {
        DNSMessageParseException exception = new DNSMessageParseException();
        Assertions.assertNotNull(exception);
    }

    @Test
    void testDNSMessageParseException_WithMessage() {
        DNSMessageParseException exception = new DNSMessageParseException("message");
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("message", exception.getMessage());
    }

    @Test
    void testDNSMessageParseException_WithMessageAndThrowable() {
        Throwable throwable = Mockito.mock(Throwable.class);
        DNSMessageParseException exception = new DNSMessageParseException("message", throwable);
        Assertions.assertNotNull(exception);
        Assertions.assertEquals("message", exception.getMessage());
        Assertions.assertEquals(throwable, exception.getCause());
    }
}
