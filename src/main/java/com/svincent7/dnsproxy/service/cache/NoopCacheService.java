package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.model.records.Record;

public class NoopCacheService extends AbstractCacheService {

    public NoopCacheService(final DnsProxyConfig config) {
        super(config);
    }

    @Override
    public DNSCacheEntry getCachedResponse(final Record question) {
        return null;
    }

    @Override
    public void cacheResponse(final Record question, final DNSCacheEntry response) {

    }
}
