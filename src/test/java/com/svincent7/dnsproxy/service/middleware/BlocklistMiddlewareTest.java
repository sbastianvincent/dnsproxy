package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.blocklist.BlocklistDictionary;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

@Slf4j
public class BlocklistMiddlewareTest {
    MessageMiddleware blocklistMiddleware;
    BlocklistDictionary blocklistDictionary;

    @BeforeEach
    void setup() {
        blocklistDictionary = Mockito.mock(BlocklistDictionary.class);
        blocklistMiddleware = new BlocklistMiddleware(blocklistDictionary);
    }

    @Test
    void testHandleInternal() throws IOException {
        Mockito.when(blocklistDictionary.isBlocked("domain.com.")).thenReturn(false);

        Record question = Mockito.mock(Record.class);
        Mockito.when(question.getName()).thenReturn(new Name("domain.com."));

        List<Record> questions = List.of(question);

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getQuestionRecords()).thenReturn(questions);

        Message msg = blocklistMiddleware.handleInternal(message);

        Assertions.assertNotNull(msg);
    }

    @Test
    void testHandleInternal_Blocked() throws IOException {
        Mockito.when(blocklistDictionary.isBlocked("domain.com.")).thenReturn(true);

        Record question = Mockito.mock(Record.class);
        Mockito.when(question.getName()).thenReturn(new Name("domain.com."));

        List<Record> questions = List.of(question);

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getQuestionRecords()).thenReturn(questions);

        Message msg = blocklistMiddleware.handleInternal(message);

        Assertions.assertNotNull(msg);
    }

    @Test
    void shouldSkipFalse() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(false);

        Assertions.assertFalse(blocklistMiddleware.shouldSkipMiddleware(message));
    }
}
