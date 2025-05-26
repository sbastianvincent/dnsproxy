package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CacheFactoryTest {
    private CacheFactory cacheFactory;
    private DnsProxyConfig config;

    @BeforeEach
    void setup() {
        config = Mockito.mock(DnsProxyConfig.class);
        cacheFactory = new CacheFactoryImpl(config);
    }

    @Test
    void testCacheStrategy_InMemory() {
        Mockito.when(config.getCacheStrategy()).thenReturn("in-memory");

        CacheService cacheService = cacheFactory.getCacheService();

        Assertions.assertTrue(cacheService instanceof InMemoryCacheService);
    }

    @Test
    void testCacheStrategy_DefaultNoop() {
        Mockito.when(config.getCacheStrategy()).thenReturn("default");

        CacheService cacheService = cacheFactory.getCacheService();

        Assertions.assertTrue(cacheService instanceof NoopCacheService);
    }
}
