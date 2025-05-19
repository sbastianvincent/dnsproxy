package com.svincent7.dnsproxy.service.dnsrewrites;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.model.Type;
import com.svincent7.dnsproxy.model.records.Record;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractDNSRewritesProvider implements DNSRewritesProvider {
    private final DnsProxyConfig config;
    private final Set<Type> allowlistedDnsRewritesTypes = new HashSet<>();
    private final Map<String, List<Record>> recordDNSRewrites = new ConcurrentHashMap<>();

    public AbstractDNSRewritesProvider(final DnsProxyConfig config) {
        this.config = config;
        init();
    }

    public void init() {
        for (String allowlist : config.getAllowlistedDnsRewritesTypes()) {
            allowlistedDnsRewritesTypes.add(Type.valueOf(allowlist.toUpperCase()));
        }
        initRecordDNSRewrites();
        log.debug("recordDNSRewrites: {}", recordDNSRewrites);
    }

    @Override
    public List<Record> getDNSRewritesAnswer(final Record question) {
        if (recordDNSRewrites.isEmpty()) {
            return null;
        }

        if (!allowlistedDnsRewritesTypes.contains(question.getType())) {
            return null;
        }

        String domain = question.getName().getName();
        return recordDNSRewrites.get(domain);
    }

    protected DnsProxyConfig getConfig() {
        return config;
    }

    protected void addRecordDNSRewrites(final String domain, final Record record) {
        List<Record> records = recordDNSRewrites.getOrDefault(domain, new ArrayList<>());
        records.add(record);
        this.recordDNSRewrites.put(domain, records);
    }

    protected abstract void initRecordDNSRewrites();
}
