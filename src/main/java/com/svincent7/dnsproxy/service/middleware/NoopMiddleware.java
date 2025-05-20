package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;

import java.io.IOException;

public class NoopMiddleware extends MessageMiddleware {

    @Override
    protected boolean shouldSkipMiddleware(final Message message) throws IOException {
        return true;
    }

    @Override
    protected Message handleInternal(final Message message) throws IOException {
        return handleNext(message);
    }
}
