package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.service.cache.CacheService;

public abstract class MessageMiddleware {
    private MessageMiddleware next;
    private final CacheService cacheService;

    public MessageMiddleware(final CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public static MessageMiddleware link(final MessageMiddleware firstMiddleware, final MessageMiddleware... chain) {
        MessageMiddleware head = firstMiddleware;
        for (MessageMiddleware middleware : chain) {
            head.next = middleware;
            head = middleware;
        }
        return firstMiddleware;
    }

    public abstract Message handle(Message message);

    protected Message handleNext(final Message message) {
        if (next == null || message.isQueryComplete()) {
            return message;
        }
        return next.handle(message);
    }

    protected CacheService getCacheService() {
        return cacheService;
    }
}
