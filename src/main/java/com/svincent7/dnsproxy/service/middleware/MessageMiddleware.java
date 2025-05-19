package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;

import java.io.IOException;

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

    public abstract Message handle(Message message) throws IOException;

    protected Message handleNext(final Message message) throws IOException {
        if (next == null ||
                (message.isQueryComplete() && (message.isReturnedFromCache() || message.isDNSRewritten()))) {
            return message;
        }
        return next.handle(message);
    }
}
