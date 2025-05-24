package com.svincent7.dnsproxy.service.blocklist;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ConfigFileBlocklistProvider implements BlocklistProvider {
    private final DnsProxyConfig config;

    @Override
    public Set<String> getBlocklists() {
        return new HashSet<>(config.getBlocklisted());
    }
}
