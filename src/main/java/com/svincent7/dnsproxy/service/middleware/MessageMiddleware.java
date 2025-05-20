package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
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

    public Message handle(final Message message) throws IOException {
        if (shouldSkipMiddleware(message)) {
            log.debug("Skipping {}", getClass().getSimpleName());
            return handleNext(message);
        }
        return handleInternal(message);
    }

    protected Message handleNext(final Message message) throws IOException {
        if (next == null) {
            return message;
        }
        return next.handle(message);
    }

    protected abstract boolean shouldSkipMiddleware(Message message) throws IOException;
    protected abstract Message handleInternal(Message message) throws IOException;
}
