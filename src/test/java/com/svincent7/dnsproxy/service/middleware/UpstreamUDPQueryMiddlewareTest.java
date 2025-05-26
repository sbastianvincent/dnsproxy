package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import com.svincent7.dnsproxy.service.resolver.Resolver;
import com.svincent7.dnsproxy.service.resolver.UDPResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class UpstreamUDPQueryMiddlewareTest {
    DNSResolverFactory dnsResolverFactory;
    UpstreamUDPQueryMiddleware upstreamUDPQueryMiddleware;

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

    @BeforeEach
    void setup() throws IOException {
        UDPResolver resolver = Mockito.mock(UDPResolver.class);
        Mockito.when(resolver.send(Mockito.any())).thenReturn(DATA);

        dnsResolverFactory = Mockito.mock(DNSResolverFactory.class);
        Mockito.when(dnsResolverFactory.createUDPResolver()).thenReturn(resolver);

        upstreamUDPQueryMiddleware = new UpstreamUDPQueryMiddleware(dnsResolverFactory);
    }

    @Test
    void testGetResolver() {
        UDPResolver resolver = Mockito.mock(UDPResolver.class);
        Mockito.when(dnsResolverFactory.createUDPResolver()).thenReturn(resolver);

        Resolver res = upstreamUDPQueryMiddleware.getResolver(dnsResolverFactory);

        Assertions.assertEquals(resolver, res);
    }

    @Test
    void testHandleInternal() throws IOException {
        Message message = Mockito.mock(Message.class);

        upstreamUDPQueryMiddleware.handleInternal(message);
    }

    @Test
    void testShouldSkip() {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(true);

        Assertions.assertTrue(upstreamUDPQueryMiddleware.shouldSkipMiddleware(message));
    }
}
