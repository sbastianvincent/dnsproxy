package com.svincent7.dnsproxy.service.resolver;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class DNSResolverFactoryTest {
    DnsProxyConfig dnsProxyConfig;
    DNSResolverFactory dnsResolverFactory;

    @BeforeEach
    void setup() {
        dnsProxyConfig = Mockito.mock(DnsProxyConfig.class);
        dnsResolverFactory = new DNSResolverFactoryImpl(dnsProxyConfig);
    }

    @Test
    void testCreateUDPResolver() {
        Mockito.when(dnsProxyConfig.getUpstreamServers()).thenReturn(List.of("1.1.1.1"));
        UDPResolver udpResolver = dnsResolverFactory.createUDPResolver();

        Assertions.assertNotNull(udpResolver);
    }

    @Test
    void testCreateUDPResolver_EmptyUpstreamServer() {
        Mockito.when(dnsProxyConfig.getUpstreamServers()).thenReturn(List.of());
        Assertions.assertThrows(IllegalArgumentException.class, () -> dnsResolverFactory.createUDPResolver());
    }

    @Test
    void testCreateTCPResolver() {
        Mockito.when(dnsProxyConfig.getUpstreamServers()).thenReturn(List.of("1.1.1.1"));
        TCPResolver tcpResolver = dnsResolverFactory.createTCPResolver();

        Assertions.assertNotNull(tcpResolver);
    }

    @Test
    void testCreateTCPResolver_EmptyUpstreamServer() {
        Mockito.when(dnsProxyConfig.getUpstreamServers()).thenReturn(List.of());
        Assertions.assertThrows(IllegalArgumentException.class, () -> dnsResolverFactory.createTCPResolver());
    }
}
