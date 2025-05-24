package com.svincent7.dnsproxy.service;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionary;
import com.svincent7.dnsproxy.service.blocklist.BlocklistDictionary;
import com.svincent7.dnsproxy.service.cache.CacheFactory;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProviderFactory;
import com.svincent7.dnsproxy.service.packet.PacketHandler;
import com.svincent7.dnsproxy.service.packet.TCPHandler;
import com.svincent7.dnsproxy.service.packet.UDPHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class DnsProxy implements SmartLifecycle {

    private final BlocklistDictionary blocklistDictionary;
    private final AllowlistDictionary allowlistDictionary;
    private final DatagramSocket udpSocket;
    private final ServerSocket tcpSocket;
    private final ExecutorService executor;
    private final CacheService cacheService;
    private final DNSResolverFactory dnsResolverFactory;
    private final DNSRewritesProvider dnsRewritesProvider;
    private final Thread listenerUDPThread;
    private final Thread listenerTCPThread;

    private volatile boolean running = false;

    private static final int BUFFER_SIZE = 1024;

    @Autowired
    public DnsProxy(final BlocklistDictionary blocklistDictionary,
                    final AllowlistDictionary allowlistDictionary,
                    final DnsProxyConfig config,
                    final CacheFactory cacheFactory,
                    final DNSResolverFactory dnsResolverFactory,
                    final DNSRewritesProviderFactory dnsRewritesProviderFactory) throws Exception {
        this.executor = Executors.newFixedThreadPool(config.getThreadPoolSize());
        this.udpSocket = new DatagramSocket(config.getPort());
        this.tcpSocket = new ServerSocket(config.getPort());
        this.blocklistDictionary = blocklistDictionary;
        this.allowlistDictionary = allowlistDictionary;
        this.cacheService = cacheFactory.getCacheService();
        this.dnsResolverFactory = dnsResolverFactory;
        this.dnsRewritesProvider = dnsRewritesProviderFactory.getDNSRewritesProvider();
        this.listenerUDPThread = new Thread(this::listenUdp);
        this.listenerUDPThread.setName("dns-udp-proxy-listener");
        this.listenerTCPThread = new Thread(this::listenTcp);
        this.listenerTCPThread.setName("dns-tcp-proxy-listener");
    }

    @Override
    public void start() {
        if (!running) {
            running = true;
            listenerUDPThread.start();
            listenerTCPThread.start();
        }
    }

    @Override
    public void stop() {
        if (running) {
            running = false;
            udpSocket.close();
            try {
                tcpSocket.close();
            } catch (IOException e) {
                log.warn("Error closing TCP socket", e);
            }
            executor.shutdownNow();
            log.info("Shutting down DNS Proxy...");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void listenUdp() {
        log.info("Starting DNS UDP Proxy on port {}", udpSocket.getLocalPort());
        byte[] buffer = new byte[BUFFER_SIZE];
        running = true;

        while (running) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                udpSocket.receive(request);
                executor.submit(() -> handleUdpRequest(request));
            } catch (IOException e) {
                if (running) {
                    log.error("Error while Running DNS Proxy", e);
                }
            }
        }

        log.info("DNS Proxy listener thread stopped.");
    }

    private void listenTcp() {
        log.info("Starting DNS TCP Proxy on port {}", tcpSocket.getLocalPort());

        while (running && !tcpSocket.isClosed()) {
            try {
                Socket socket = tcpSocket.accept();
                log.debug("Accepted TCP connection from {}", socket.getRemoteSocketAddress());
                executor.submit(() -> handleTcpRequest(socket));
            } catch (IOException e) {
                if (running) {
                    log.error("Error accepting TCP connection", e);
                }
            }
        }

        log.info("TCP Proxy listener thread stopped.");
    }

    private void handleUdpRequest(final DatagramPacket request) {
        try {
            PacketHandler handler = new UDPHandler(
                    blocklistDictionary,
                    allowlistDictionary,
                    udpSocket,
                    request,
                    cacheService,
                    dnsResolverFactory,
                    dnsRewritesProvider
            );
            handler.handlePacket();
        } catch (IOException e) {
            log.error("Error while handling UDP Packet", e);
        }
    }

    private void handleTcpRequest(final Socket socket) {
        try {
            PacketHandler handler = new TCPHandler(
                    blocklistDictionary,
                    allowlistDictionary,
                    socket,
                    cacheService,
                    dnsResolverFactory,
                    dnsRewritesProvider
            );
            handler.handlePacket();
        } catch (IOException e) {
            log.error("Error handling TCP DNS request", e);
        }
    }

}
