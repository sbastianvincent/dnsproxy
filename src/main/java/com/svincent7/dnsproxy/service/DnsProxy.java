package com.svincent7.dnsproxy.service;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.service.cache.CacheFactory;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.dnsclient.DNSUDPClientFactory;
import com.svincent7.dnsproxy.service.packet.PacketHandler;
import com.svincent7.dnsproxy.service.packet.UDPHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class DnsProxy {

    private final DatagramSocket socket;
    private final ExecutorService executor;
    private final CacheService cacheService;
    private final DNSUDPClientFactory dnsudpClientFactory;

    private boolean running = false;

    private static final int BUFFER_SIZE = 1024;

    @Autowired
    public DnsProxy(final DnsProxyConfig config, final CacheFactory cacheFactory,
                    final DNSUDPClientFactory dnsudpClientFactory) throws Exception {
        this.executor = Executors.newFixedThreadPool(config.getThreadPoolSize());
        this.socket = new DatagramSocket(config.getPort());
        this.cacheService = cacheFactory.getCacheService();
        this.dnsudpClientFactory = dnsudpClientFactory;
    }

    @PostConstruct
    public void start() {
        final Thread thread = new Thread(() -> {
            try {
                running = true;
                byte[] buffer = new byte[BUFFER_SIZE];

                log.info("Starting DNS Proxy on port {}", socket.getLocalPort());

                while (running) {
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    executor.submit(() -> {
                        try {
                            PacketHandler handler = new UDPHandler(socket, request, cacheService,
                                    dnsudpClientFactory.createDNSUDPClient());
                            handler.handlePacket();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
            } finally {
                closeSocket();
            }
        });
        thread.start();
    }

    @PreDestroy
    public void destroy() {
        closeSocket();
    }

    private void closeSocket() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
