package com.svincent7.dnsproxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class DnsProxyBean {
    private final DnsProxyConfig dnsProxyConfig;

    @Bean
    public DatagramSocket defaultDatagramSocket() throws SocketException {
        return new DatagramSocket(dnsProxyConfig.getPort());
    }

    @Bean
    public ServerSocket defaultServerSocket() throws IOException {
        return new ServerSocket(dnsProxyConfig.getPort());
    }

    @Bean
    public ExecutorService defaultExecutorService() {
        return Executors.newFixedThreadPool(dnsProxyConfig.getThreadPoolSize());
    }
}
