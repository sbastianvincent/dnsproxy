package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheFactoryImpl implements CacheFactory {
    private final DnsProxyConfig config;

    private static final String IN_MEMORY_CACHE = "in-memory";

    @Override
    public CacheService getCacheService() {
        log.info("Create cache service with strategy: {}", config.getCacheStrategy());
        return switch (config.getCacheStrategy()) {
            case IN_MEMORY_CACHE -> new InMemoryCacheService();
            default -> new NoopCacheService();
        };
    }
}
