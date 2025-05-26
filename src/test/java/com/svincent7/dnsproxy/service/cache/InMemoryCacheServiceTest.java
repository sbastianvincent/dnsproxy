package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.config.DnsProxyConfig;
import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import com.svincent7.dnsproxy.model.records.ARecord;
import com.svincent7.dnsproxy.model.records.QRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class InMemoryCacheServiceTest {
    private InMemoryCacheService inMemoryCacheService;

    @BeforeEach
    void setup() {
        DnsProxyConfig config = Mockito.mock(DnsProxyConfig.class);
        inMemoryCacheService = new InMemoryCacheService(config);
    }

    @Test
    void testGetCachedResponse() {
        QRecord question = new QRecord(new Name("domain.com"), Type.A, DNSClass.IN);
        ARecord answer = Mockito.mock(ARecord.class);
        DNSCacheEntry entry = new DNSCacheEntry();
        entry.setAnswer(answer);
        entry.setExpiredTime(System.currentTimeMillis() + 30000);

        inMemoryCacheService.cacheResponse(question, entry);

        DNSCacheEntry cacheEntry = inMemoryCacheService.getCachedResponse(question);

        Assertions.assertNotNull(cacheEntry);
        Assertions.assertEquals(answer, cacheEntry.getAnswer());
    }

    @Test
    void testGetCachedResponse_Expired() {
        QRecord question = new QRecord(new Name("domain.com"), Type.A, DNSClass.IN);
        ARecord answer = Mockito.mock(ARecord.class);
        DNSCacheEntry entry = new DNSCacheEntry();
        entry.setAnswer(answer);
        entry.setExpiredTime(System.currentTimeMillis() - 30000);

        inMemoryCacheService.cacheResponse(question, entry);

        DNSCacheEntry cacheEntry = inMemoryCacheService.getCachedResponse(question);

        Assertions.assertNull(cacheEntry);
    }

    @Test
    void testGetCachedResponse_Uncached() {
        QRecord question = new QRecord(new Name("domain.com"), Type.A, DNSClass.IN);

        DNSCacheEntry dnsCacheEntry = inMemoryCacheService.getCachedResponse(question);

        Assertions.assertNull(dnsCacheEntry);
    }
}
