package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.cache.DNSCacheEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class CacheLookupMiddleware extends MessageMiddleware {
    private final CacheService cacheService;

    public CacheLookupMiddleware(final CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    protected Message handleInternal(final Message message) throws IOException {
        List<Record> questions = message.getQuestionRecords();
        for (Record record : questions) {
            DNSCacheEntry dnsCacheEntry = cacheService.getCachedResponse(record);
            if (dnsCacheEntry == null) {
                continue;
            }
            log.debug("Got dnsCacheEntry: {}", dnsCacheEntry);
            message.addAnswerRecord(dnsCacheEntry.getAnswer());
            message.setReturnedFromCache(true);
        }
        return handleNext(message);
    }

    @Override
    protected boolean shouldSkipMiddleware(final Message msg) {
        return msg.isQueryComplete();
    }
}
