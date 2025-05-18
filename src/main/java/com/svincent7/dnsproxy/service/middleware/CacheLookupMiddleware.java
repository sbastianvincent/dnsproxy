package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.service.cache.CacheService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheLookupMiddleware extends MessageMiddleware {

    public CacheLookupMiddleware(final CacheService cacheService) {
        super(cacheService);
    }

    @Override
    public Message handle(final Message message) {
        Message cachedMessage = getCacheService().getCachedResponse(message);
        if (cachedMessage == null) {
            return handleNext(message);
        }
        log.debug("Got cached response: {}", cachedMessage);
        return handleNext(Message.fromCachedMessage(message, cachedMessage));
    }
}
