package com.svincent7.dnsproxy.service;

import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionary;
import com.svincent7.dnsproxy.service.blocklist.BlocklistDictionary;
import com.svincent7.dnsproxy.service.cache.CacheFactory;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProviderFactory;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import com.svincent7.dnsproxy.service.resolver.TCPResolver;
import com.svincent7.dnsproxy.service.resolver.UDPResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class DnsProxyTest {
    private DnsProxy dnsProxy;
    private static final int TEST_PORT = 12345;
    final ExecutorService executorService = Mockito.mock(ExecutorService.class);
    final DatagramSocket datagramSocket = Mockito.mock(DatagramSocket.class);
    final ServerSocket serverSocket = Mockito.mock(ServerSocket.class);
    final BlocklistDictionary blocklistDictionary = Mockito.mock(BlocklistDictionary.class);
    final AllowlistDictionary allowlistDictionary = Mockito.mock(AllowlistDictionary.class);
    final CacheFactory cacheFactory = Mockito.mock(CacheFactory.class);
    final DNSResolverFactory dnsResolverFactory = Mockito.mock(DNSResolverFactory.class);
    final DNSRewritesProviderFactory dnsRewritesProviderFactory = Mockito.mock(DNSRewritesProviderFactory.class);

    @BeforeEach
    public void setUp() throws Exception {
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(serverSocket.accept()).thenReturn(socket);
        Mockito.when(socket.getRemoteSocketAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 54321));

        dnsProxy = new DnsProxy(executorService, datagramSocket, serverSocket, blocklistDictionary, allowlistDictionary,
                cacheFactory, dnsResolverFactory, dnsRewritesProviderFactory);
    }

    @Test
    public void testStartStop() {
        Assertions.assertFalse(dnsProxy.isRunning());
        dnsProxy.start();
        Assertions.assertTrue(dnsProxy.isRunning());

        dnsProxy.start();
        Assertions.assertTrue(dnsProxy.isRunning());

        dnsProxy.stop();
        Assertions.assertFalse(dnsProxy.isRunning());

        dnsProxy.stop();
        Assertions.assertFalse(dnsProxy.isRunning());
    }

    @Test
    public void testListenUdp() {
        Mockito.when(serverSocket.isClosed()).thenReturn(true);

        Assertions.assertFalse(dnsProxy.isRunning());
        dnsProxy.start();
        Assertions.assertTrue(dnsProxy.isRunning());

        byte[] request = new byte[] {1, 2, 3, 4};
        UDPResolver udpResolver = new UDPResolver("127.0.0.1", TEST_PORT);
        Assertions.assertThrows(IOException.class, () -> udpResolver.send(request));

        dnsProxy.stop();
        Assertions.assertFalse(dnsProxy.isRunning());
    }

    @Test
    public void testListenUdp_ThrowIOException() throws IOException {
        Mockito.when(serverSocket.isClosed()).thenReturn(true);
        Mockito.doThrow(IOException.class).when(datagramSocket).receive(Mockito.any());

        Assertions.assertFalse(dnsProxy.isRunning());
        dnsProxy.start();
        Assertions.assertTrue(dnsProxy.isRunning());

        byte[] request = new byte[] {1, 2, 3, 4};
        UDPResolver udpResolver = new UDPResolver("127.0.0.1", TEST_PORT);
        Assertions.assertThrows(IOException.class, () -> udpResolver.send(request));

        dnsProxy.stop();
        Assertions.assertFalse(dnsProxy.isRunning());
    }

    @Test
    public void testListenTcp() {
        Mockito.when(serverSocket.isClosed()).thenReturn(false);
        Assertions.assertFalse(dnsProxy.isRunning());
        dnsProxy.start();
        Assertions.assertTrue(dnsProxy.isRunning());

        byte[] request = new byte[] {1, 2, 3, 4};
        TCPResolver tcpResolver = new TCPResolver("127.0.0.1", TEST_PORT);
        Assertions.assertThrows(IOException.class, () -> tcpResolver.send(request));

        dnsProxy.stop();
        Assertions.assertFalse(dnsProxy.isRunning());
    }

    @Test
    public void testListenTcp_ThrowIOException() throws IOException {
        Mockito.when(serverSocket.isClosed()).thenReturn(false);
        Mockito.when(serverSocket.accept()).thenThrow(IOException.class);

        Assertions.assertFalse(dnsProxy.isRunning());
        dnsProxy.start();
        Assertions.assertTrue(dnsProxy.isRunning());

        byte[] request = new byte[] {1, 2, 3, 4};
        TCPResolver tcpResolver = new TCPResolver("127.0.0.1", TEST_PORT);
        Assertions.assertThrows(IOException.class, () -> tcpResolver.send(request));

        dnsProxy.stop();
        Assertions.assertFalse(dnsProxy.isRunning());
    }

    @Test
    public void testListenTcp_TcpSocketClosed() throws IOException {
        Mockito.when(serverSocket.isClosed()).thenReturn(true);
        Mockito.when(serverSocket.accept()).thenThrow(IOException.class);

        Assertions.assertFalse(dnsProxy.isRunning());
        dnsProxy.start();
        Assertions.assertTrue(dnsProxy.isRunning());

        byte[] request = new byte[] {1, 2, 3, 4};
        TCPResolver tcpResolver = new TCPResolver("127.0.0.1", TEST_PORT);
        Assertions.assertThrows(IOException.class, () -> tcpResolver.send(request));

        dnsProxy.stop();
        Assertions.assertFalse(dnsProxy.isRunning());
    }

    @Test
    void testStop_TcpSocketThrowException() throws IOException {
        Mockito.doThrow(IOException.class).when(serverSocket).close();

        Assertions.assertFalse(dnsProxy.isRunning());
        dnsProxy.start();
        Assertions.assertTrue(dnsProxy.isRunning());
        dnsProxy.stop();
        Assertions.assertFalse(dnsProxy.isRunning());
    }

    @Test
    void testHandleUdpRequest() throws IOException {
        DatagramPacket packet = Mockito.mock(DatagramPacket.class);
        Mockito.when(packet.getData()).thenReturn(new byte[512]);
        dnsProxy.handleUdpRequest(packet);
    }

    @Test
    void testHandleUdpRequest_GotException() {
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

        DatagramPacket packet = Mockito.mock(DatagramPacket.class);
        Mockito.when(packet.getData()).thenReturn(httpsResponse);
        dnsProxy.handleUdpRequest(packet);
    }

    @Test
    void testHandleTcpRequest() throws IOException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Socket socket = Mockito.mock(Socket.class);

        Mockito.when(socket.getInputStream()).thenReturn(inputStream);
        Mockito.when(inputStream.readNBytes(Mockito.anyInt())).thenReturn(new byte[512]);
        Mockito.when(socket.getOutputStream()).thenReturn(Mockito.mock(OutputStream.class));

        dnsProxy.handleTcpRequest(socket);
    }

    @Test
    void testHandleTcpRequest_GotException() throws IOException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Socket socket = Mockito.mock(Socket.class);

        Mockito.when(socket.getInputStream()).thenReturn(inputStream);
        Mockito.when(inputStream.readNBytes(Mockito.anyInt())).thenReturn(new byte[0]);
        Mockito.when(socket.getOutputStream()).thenReturn(Mockito.mock(OutputStream.class));

        dnsProxy.handleTcpRequest(socket);
    }
}
