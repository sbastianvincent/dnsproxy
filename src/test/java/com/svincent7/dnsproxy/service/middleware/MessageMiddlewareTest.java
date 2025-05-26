package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class MessageMiddlewareTest {
    MessageMiddleware messageMiddleware;

    @BeforeEach
    void setup() {
        messageMiddleware = MessageMiddleware.link(new MessageMiddleware() {
            @Override
            protected boolean shouldSkipMiddleware(Message message) throws IOException {
                return false;
            }

            @Override
            protected Message handleInternal(Message message) throws IOException {
                return message;
            }
        }, new MessageMiddleware() {

            @Override
            protected boolean shouldSkipMiddleware(Message message) throws IOException {
                return false;
            }

            @Override
            protected Message handleInternal(Message message) throws IOException {
                return message;
            }
        });
    }

    @Test
    void testBlockedResponse() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isBlockedResponse()).thenReturn(true);

        Message msg = messageMiddleware.handleNext(message);

        Assertions.assertNotNull(message);
        Assertions.assertEquals(msg, message);
    }

}
