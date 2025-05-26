package com.svincent7.dnsproxy.service.packet;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionary;
import com.svincent7.dnsproxy.service.blocklist.BlocklistDictionary;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPHandlerTest {
    TCPHandler tcpHandler;
    BlocklistDictionary blocklistDictionary;
    AllowlistDictionary allowlistDictionary;
    Socket socket;
    CacheService cacheService;
    DNSResolverFactory dnsResolverFactory;
    DNSRewritesProvider dnsRewritesProvider;

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
    void setup() {
        blocklistDictionary = Mockito.mock(BlocklistDictionary.class);
        allowlistDictionary = Mockito.mock(AllowlistDictionary.class);
        socket = Mockito.mock(Socket.class);
        cacheService = Mockito.mock(CacheService.class);
        dnsResolverFactory = Mockito.mock(DNSResolverFactory.class);
        dnsRewritesProvider = Mockito.mock(DNSRewritesProvider.class);

        tcpHandler = new TCPHandler(blocklistDictionary, allowlistDictionary, socket, cacheService, dnsResolverFactory, dnsRewritesProvider);
    }

    @Test
    void testGetMaxPacketSize() {
        Assertions.assertEquals(65536, tcpHandler.getMaxPacketSize());
    }

    @Test
    void testGetMessageFromInput() throws IOException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.readNBytes(Mockito.anyInt())).thenReturn(DATA);
        Mockito.when(socket.getInputStream()).thenReturn(inputStream);

        Message message = tcpHandler.getMessageFromInput();

        Assertions.assertNotNull(message);
    }

    @Test
    void testGetMessageFromInput_LengthBytesUnder2_ThrowException() throws IOException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        byte[] res = new byte[0];
        Mockito.when(inputStream.readNBytes(2)).thenReturn(res);
        Mockito.when(socket.getInputStream()).thenReturn(inputStream);

        Assertions.assertThrows(DNSMessageParseException.class, () -> tcpHandler.getMessageFromInput());
    }

    @Test
    void testSendResponse() throws IOException {
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Mockito.when(socket.getOutputStream()).thenReturn(outputStream);

        tcpHandler.sendResponse(new MessageOutput());
    }

    @Test
    void testHandlePacket() throws IOException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.readNBytes(Mockito.anyInt())).thenReturn(DATA);
        Mockito.when(socket.getInputStream()).thenReturn(inputStream);

        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Mockito.when(socket.getOutputStream()).thenReturn(outputStream);

        tcpHandler.handlePacket();
    }
}
