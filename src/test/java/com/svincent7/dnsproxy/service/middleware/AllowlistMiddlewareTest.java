package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionary;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

@Slf4j
public class AllowlistMiddlewareTest {
    MessageMiddleware allowlistMiddleware;
    AllowlistDictionary allowlistDictionary;

    @BeforeEach
    void setup() {
        allowlistDictionary = Mockito.mock(AllowlistDictionary.class);
        allowlistMiddleware = new AllowlistMiddleware(allowlistDictionary);
    }

    @Test
    void testHandleInternal() throws IOException {
        Mockito.when(allowlistDictionary.isAllowed("domain.com.")).thenReturn(true);

        Record question = Mockito.mock(Record.class);
        Mockito.when(question.getName()).thenReturn(new Name("domain.com."));

        List<Record> questions = List.of(question);

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getQuestionRecords()).thenReturn(questions);

        Message msg = allowlistMiddleware.handleInternal(message);

        Assertions.assertNotNull(msg);
    }

    @Test
    void testHandleInternal_Blocked() throws IOException {
        Mockito.when(allowlistDictionary.isAllowed("domain.com.")).thenReturn(false);

        Record question = Mockito.mock(Record.class);
        Mockito.when(question.getName()).thenReturn(new Name("domain.com."));

        List<Record> questions = List.of(question);

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getQuestionRecords()).thenReturn(questions);

        Message msg = allowlistMiddleware.handleInternal(message);

        Assertions.assertNotNull(msg);
    }

    @Test
    void shouldSkipFalse() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(false);

        Assertions.assertFalse(allowlistMiddleware.shouldSkipMiddleware(message));
    }
}
