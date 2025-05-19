package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.util.CryptoUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCacheService extends AbstractCacheService {
    private final Map<String, DNSCacheEntry> cachedResponse = new ConcurrentHashMap<>();

    public InMemoryCacheService(final DnsProxyConfig config) {
        super(config);
    }

    @Override
    public DNSCacheEntry getCachedResponse(final Record question) {
        String key = generateKey(question);
        DNSCacheEntry entry = cachedResponse.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.getExpiredTime() < System.currentTimeMillis()) {
            cachedResponse.remove(key);
            return null;
        }
        return entry;
    }

    @Override
    public void cacheResponse(final Record question, final DNSCacheEntry response) {
        String key = generateKey(question);
        cachedResponse.put(key, response);
    }

    private String generateKey(final Record record) {
        String queriedRecord = record.getName() +
                ":" +
                record.getType().getValue() +
                ":" +
                record.getDnsClass().getValue();

        return CryptoUtils.sha256(queriedRecord);
    }
}
