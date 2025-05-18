package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.service.cache.CacheService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheLookupMiddleware extends MessageMiddleware {
    private final CacheService cacheService;

    public CacheLookupMiddleware(final CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public Message handle(final Message message) {
        Message cachedMessage = cacheService.getCachedResponse(message);
        if (cachedMessage == null) {
            return handleNext(message);
        }
        log.debug("Got cached response: {}", cachedMessage);
        return handleNext(Message.fromCachedMessage(message, cachedMessage));
    }
}
