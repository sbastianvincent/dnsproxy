package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.cache.DNSCacheEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

public class CacheLookupMiddlewareTest {
    private CacheService cacheService;
    private CacheLookupMiddleware cacheLookupMiddleware;

    @BeforeEach
    void test() {
        cacheService = Mockito.mock(CacheService.class);
        cacheLookupMiddleware = new CacheLookupMiddleware(cacheService);
    }

    @Test
    void testHandleInternal() throws IOException {
        Record question = Mockito.mock(Record.class);
        Record question2 = Mockito.mock(Record.class);

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getQuestionRecords()).thenReturn(List.of(question, question2));
        Mockito.when(cacheService.getCachedResponse(question)).thenReturn(null);
        Mockito.when(cacheService.getCachedResponse(question2)).thenReturn(Mockito.mock(DNSCacheEntry.class));

        Message msg = cacheLookupMiddleware.handleInternal(message);

        Assertions.assertNotNull(msg);
    }

    @Test
    void shouldSkipFalse() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(false);

        Assertions.assertFalse(cacheLookupMiddleware.shouldSkipMiddleware(message));
    }

}
