package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class NoopMiddlewareTest {
    NoopMiddleware noopMiddleware;

    @BeforeEach
    void setup() {
        noopMiddleware = new NoopMiddleware();
    }

    @Test
    void testHandleInternal() throws IOException {
        Message message = Mockito.mock(Message.class);

        Message msg = noopMiddleware.handleInternal(message);

        Assertions.assertNotNull(msg);
    }

    @Test
    void testShouldSkip() throws IOException {
        Assertions.assertTrue(noopMiddleware.shouldSkipMiddleware(Mockito.mock(Message.class)));
    }
}
