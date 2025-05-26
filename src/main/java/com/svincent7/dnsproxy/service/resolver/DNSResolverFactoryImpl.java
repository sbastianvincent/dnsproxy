package com.svincent7.dnsproxy.service.resolver;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DNSResolverFactoryImpl implements DNSResolverFactory {

    private final DnsProxyConfig dnsProxyConfig;

    private static final int DNS_DEFAULT_PORT = 53;

    @Override
    public UDPResolver createUDPResolver() {
        Random random = new Random();
        List<String> upstreamServers = dnsProxyConfig.getUpstreamServers();

        if (upstreamServers.isEmpty()) {
            throw new IllegalArgumentException("No upstream server found");
        }

        String upstreamServer = upstreamServers.get(random.nextInt(upstreamServers.size()));
        return new UDPResolver(upstreamServer, DNS_DEFAULT_PORT);
    }

    @Override
    public TCPResolver createTCPResolver() {
        Random random = new Random();
        List<String> upstreamServers = dnsProxyConfig.getUpstreamServers();

        if (upstreamServers.isEmpty()) {
            throw new IllegalArgumentException("No upstream server found");
        }

        String upstreamServer = upstreamServers.get(random.nextInt(upstreamServers.size()));
        return new TCPResolver(upstreamServer, DNS_DEFAULT_PORT);
    }
}
