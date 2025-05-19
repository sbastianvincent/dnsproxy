package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.cache.DNSCacheEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CacheAnswerMiddleware extends MessageMiddleware {
    private final CacheService cacheService;

    private static final int MILLIS_PER_SECOND = 1000;

    public CacheAnswerMiddleware(final CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public Message handle(final Message message) {
        if (!message.isQueryComplete()) {
            return handleNext(message);
        }

        List<Record> questions = message.getQuestionRecords();
        List<Record> answers = message.getAnswerRecords();
        for (Record question : questions) {
            if (!cacheService.getAllowlistedCacheTypes().contains(question.getType())) {
                continue;
            }

            for (Record answer : answers) {
                if (answer.getType().equals(question.getType())) {
                    log.debug("Caching answer for {}: {}", question, answer);
                    DNSCacheEntry dnsCacheEntry = new DNSCacheEntry();
                    dnsCacheEntry.setAnswer(answer);
                    dnsCacheEntry.setExpiredTime(System.currentTimeMillis() + answer.getTtl() * MILLIS_PER_SECOND);
                    cacheService.cacheResponse(question, dnsCacheEntry);
                }
            }
        }
        return handleNext(message);
    }
}
