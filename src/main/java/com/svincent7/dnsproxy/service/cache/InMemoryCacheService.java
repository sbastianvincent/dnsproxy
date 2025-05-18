package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.model.Header;
import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.util.CryptoUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InMemoryCacheService implements CacheService {
    private final Map<String, Message> cachedResponse = new HashMap<>();

    @Override
    public Message getCachedResponse(final Message message) {
        String key = generateKey(message);
        return cachedResponse.get(key);
    }

    @Override
    public void cacheResponse(final Message message) {
        String key = generateKey(message);
        cachedResponse.put(key, message);
    }

    private String generateKey(final Message message) {
        List<Record> questions = message.getSections().get(Header.SECTION_QUESTION);
        if (questions == null || questions.isEmpty()) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Record question : questions) {
            stringBuilder
                    .append(question.getName())
                    .append(":")
                    .append(question.getType().getValue())
                    .append(":")
                    .append(question.getDnsClass().getValue());
        }

        return CryptoUtils.sha256(stringBuilder.toString());
    }
}
