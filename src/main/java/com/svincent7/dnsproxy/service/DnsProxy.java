package com.svincent7.dnsproxy.service;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.service.cache.CacheFactory;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.dnsclient.DNSUDPClientFactory;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProviderFactory;
import com.svincent7.dnsproxy.service.packet.PacketHandler;
import com.svincent7.dnsproxy.service.packet.UDPHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class DnsProxy implements SmartLifecycle {

    private final DatagramSocket socket;
    private final ExecutorService executor;
    private final CacheService cacheService;
    private final DNSUDPClientFactory dnsudpClientFactory;
    private final DNSRewritesProvider dnsRewritesProvider;
    private final Thread listenerThread;

    private volatile boolean running = false;

    private static final int BUFFER_SIZE = 1024;

    @Autowired
    public DnsProxy(final DnsProxyConfig config, final CacheFactory cacheFactory,
                    final DNSUDPClientFactory dnsudpClientFactory,
                    final DNSRewritesProviderFactory dnsRewritesProviderFactory) throws Exception {
        this.executor = Executors.newFixedThreadPool(config.getThreadPoolSize());
        this.socket = new DatagramSocket(config.getPort());
        this.cacheService = cacheFactory.getCacheService();
        this.dnsudpClientFactory = dnsudpClientFactory;
        this.dnsRewritesProvider = dnsRewritesProviderFactory.getDNSRewritesProvider();
        this.listenerThread = new Thread(this::listen);
        this.listenerThread.setName("dns-proxy-listener");
    }

    @Override
    public void start() {
        if (!running) {
            running = true;
            listenerThread.start();
        }
    }

    @Override
    public void stop() {
        if (running) {
            running = false;
            socket.close();
            executor.shutdownNow();
            log.info("Shutting down DNS Proxy...");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void listen() {
        log.info("Starting DNS Proxy on port {}", socket.getLocalPort());
        byte[] buffer = new byte[BUFFER_SIZE];
        running = true;

        while (running) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(request);
                executor.submit(() -> handleRequest(request));
            } catch (IOException e) {
                if (running) {
                    log.error("Error while Running DNS Proxy", e);
                }
            }
        }

        log.info("DNS Proxy listener thread stopped.");
    }

    private void handleRequest(final DatagramPacket request) {
        try {
            PacketHandler handler = new UDPHandler(
                    socket, request, cacheService,
                    dnsudpClientFactory.createDNSUDPClient(),
                    dnsRewritesProvider
            );
            handler.handlePacket();
        } catch (IOException e) {
            log.error("Error while handling UDP Packet", e);
        }
    }
}
