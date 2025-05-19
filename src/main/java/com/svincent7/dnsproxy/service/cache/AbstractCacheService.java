package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.model.Type;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCacheService implements CacheService {
    private final DnsProxyConfig config;

    @Getter
    private final Set<Type> allowlistedCacheTypes = new HashSet<>();

    public AbstractCacheService(final DnsProxyConfig config) {
        this.config = config;
        init();
    }

    public void init() {
        for (String allowlist : config.getAllowlistedCacheType()) {
            allowlistedCacheTypes.add(Type.valueOf(allowlist.toUpperCase()));
        }
    }
}
