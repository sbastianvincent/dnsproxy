package com.svincent7.dnsproxy.service.alllowlist;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ConfigFileAllowlistProvider implements AllowlistProvider {
    private final DnsProxyConfig config;

    @Override
    public Set<String> getAllowlist() {
        return new HashSet<>(config.getAllowlistedDomains());
    }
}
