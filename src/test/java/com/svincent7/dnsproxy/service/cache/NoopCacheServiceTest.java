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

public class NoopCacheServiceTest {
    private NoopCacheService noopCacheService;

    @BeforeEach
    void setup() {
        DnsProxyConfig config = Mockito.mock(DnsProxyConfig.class);
        noopCacheService = new NoopCacheService(config);
    }

    @Test
    void testCachedResponse_ReturnNull_EvenCacheResponseWasCalled_AndNotExpired() {
        QRecord question = new QRecord(new Name("domain.com"), Type.A, DNSClass.IN);
        ARecord answer = Mockito.mock(ARecord.class);
        DNSCacheEntry entry = new DNSCacheEntry();
        entry.setAnswer(answer);
        entry.setExpiredTime(System.currentTimeMillis() + 30000);

        noopCacheService.cacheResponse(question, entry);
        DNSCacheEntry dnsCacheEntry = noopCacheService.getCachedResponse(question);

        Assertions.assertNull(dnsCacheEntry);
    }
}
