package com.svincent7.dnsproxy.service.dnsrewrites;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DNSRewritesProviderFactoryImpl implements DNSRewritesProviderFactory {

    private final DnsProxyConfig config;

    @Override
    public DNSRewritesProvider getDNSRewritesProvider() {
        log.debug("Getting DNS Rewrites provider with: {}", config.getCacheStrategy());
        return switch (config.getDnsRewritesProvider()) {
            default -> new ConfigFileDnsRewritesProvider(config);
        };
    }
}
