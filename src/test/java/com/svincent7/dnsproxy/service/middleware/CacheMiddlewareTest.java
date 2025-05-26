package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.Type;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.cache.CacheService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class CacheMiddlewareTest {
    private CacheService cacheService;
    private CacheAnswerMiddleware cacheAnswerMiddleware;

    @BeforeEach
    void test() {
        cacheService = Mockito.mock(CacheService.class);
        cacheAnswerMiddleware = new CacheAnswerMiddleware(cacheService);
    }

    @Test
    void testHandleInternal() throws IOException {
        Mockito.when(cacheService.getAllowlistedCacheTypes()).thenReturn(Set.of(Type.A, Type.CNAME));
        Record question = Mockito.mock(Record.class);
        Mockito.when(question.getType()).thenReturn(Type.A);
        Record question2 = Mockito.mock(Record.class);
        Mockito.when(question2.getType()).thenReturn(Type.AAAA);

        Record answer = Mockito.mock(Record.class);
        Mockito.when(answer.getType()).thenReturn(Type.A);
        Record answer2 = Mockito.mock(Record.class);
        Mockito.when(answer2.getType()).thenReturn(Type.AAAA);
        Record answer3 = Mockito.mock(Record.class);
        Mockito.when(answer3.getType()).thenReturn(Type.CNAME);

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getQuestionRecords()).thenReturn(List.of(question, question2));
        Mockito.when(message.getAnswerRecords()).thenReturn(List.of(answer, answer2, answer3));

        Message msg = cacheAnswerMiddleware.handleInternal(message);

        Assertions.assertNotNull(msg);
    }

    @Test
    void should_IsQueryCompleteFalse_IsDNSRewrittenFalse_IsReturnedFromCacheFalse_SkipTrue() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(false);
        Mockito.when(message.isDNSRewritten()).thenReturn(false);
        Mockito.when(message.isReturnedFromCache()).thenReturn(false);

        Assertions.assertTrue(cacheAnswerMiddleware.shouldSkipMiddleware(message));
    }

    @Test
    void should_IsQueryCompleteTrue_IsDNSRewrittenFalse_IsReturnedFromCacheFalse_SkipFalse() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(true);
        Mockito.when(message.isDNSRewritten()).thenReturn(false);
        Mockito.when(message.isReturnedFromCache()).thenReturn(false);

        Assertions.assertFalse(cacheAnswerMiddleware.shouldSkipMiddleware(message));
    }

    @Test
    void should_IsQueryCompleteTrue_IsDNSRewrittenTrue_IsReturnedFromCacheFalse_SkipTrue() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(true);
        Mockito.when(message.isDNSRewritten()).thenReturn(true);
        Mockito.when(message.isReturnedFromCache()).thenReturn(false);

        Assertions.assertTrue(cacheAnswerMiddleware.shouldSkipMiddleware(message));
    }

    @Test
    void should_IsQueryCompleteTrue_IsDNSRewrittenFalse_IsReturnedFromCacheTrue_SkipFalse() throws IOException {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.isQueryComplete()).thenReturn(true);
        Mockito.when(message.isDNSRewritten()).thenReturn(false);
        Mockito.when(message.isReturnedFromCache()).thenReturn(true);

        Assertions.assertTrue(cacheAnswerMiddleware.shouldSkipMiddleware(message));
    }
}
