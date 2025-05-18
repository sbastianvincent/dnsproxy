package com.svincent7.dnsproxy.service.dnsclient;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DNSUDPClientFactoryImpl implements DNSUDPClientFactory {

    private final DnsProxyConfig dnsProxyConfig;

    private static final int DNS_DEFAULT_PORT = 53;

    @Override
    public DNSUDPClient createDNSUDPClient() {
        Random random = new Random();
        List<String> upstreamServers = dnsProxyConfig.getUpstreamServers();
        String upstreamServer = upstreamServers.get(random.nextInt(upstreamServers.size()));

        if (upstreamServer == null) {
            throw new IllegalArgumentException("No upstream server found");
        }

        return new DNSUDPClient(upstreamServer, DNS_DEFAULT_PORT);
    }
}
