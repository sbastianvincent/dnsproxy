package com.svincent7.dnsproxy.service.dnsrewrites;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.model.Type;
import com.svincent7.dnsproxy.model.records.QRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigFileDnsRewritesProvider implements DNSRewritesProvider {

    private final DnsProxyConfig config;

    public ConfigFileDnsRewritesProvider(final DnsProxyConfig config) {
        this.config = config;
    }

    @Override
    public DNSRewrites getDNSRewrites(final QRecord record) {
        if (config.getDnsRewrites() == null) {
            return null;
        }

        log.info("record: {}", record);
        // Only Support A Record for now
        if (!record.getType().equals(Type.A)) {
            return null;
        }

        String domain = record.getName().getName();

        // Remove trailing dot only if it exists because config file can't have "." in the last character
        String query = domain.endsWith(".") ? domain.substring(0, domain.length() - 1) : domain;
        String answer = config.getDnsRewrites().get(query);
        if (answer == null) {
            return null;
        }
        return new DNSRewrites(record, answer);
    }
}
