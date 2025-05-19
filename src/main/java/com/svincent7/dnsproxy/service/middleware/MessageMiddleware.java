package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;

public abstract class MessageMiddleware {
    private MessageMiddleware next;

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
        if (next == null || (message.isQueryComplete() && message.isReturnedFromCache())) {
            return message;
        }
        return next.handle(message);
    }
}
