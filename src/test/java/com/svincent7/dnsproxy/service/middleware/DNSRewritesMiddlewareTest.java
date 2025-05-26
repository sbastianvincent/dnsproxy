package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

public class DNSRewritesMiddlewareTest {
    private DNSRewritesProvider dNSRewritesProvider;
    private DNSRewritesMiddleware dNSRewritesMiddleware;

    @BeforeEach
    void test() {
        dNSRewritesProvider = Mockito.mock(DNSRewritesProvider.class);
        dNSRewritesMiddleware = new DNSRewritesMiddleware(dNSRewritesProvider);
    }

    @Test
    void testHandleInternal() throws IOException {
        Record question = Mockito.mock(Record.class);
        Record question2 = Mockito.mock(Record.class);

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getQuestionRecords()).thenReturn(List.of(question, question2));
        Mockito.when(dNSRewritesProvider.getDNSRewritesAnswer(question)).thenReturn(null);
        Mockito.when(dNSRewritesProvider.getDNSRewritesAnswer(question2)).thenReturn(List.of(Mockito.mock(Record.class)));

        Message msg = dNSRewritesMiddleware.handleInternal(message);

        Assertions.assertNotNull(msg);
    }

    @Test
    void shouldSkipFalse() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(false);

        Assertions.assertFalse(dNSRewritesMiddleware.shouldSkipMiddleware(message));
    }

}
