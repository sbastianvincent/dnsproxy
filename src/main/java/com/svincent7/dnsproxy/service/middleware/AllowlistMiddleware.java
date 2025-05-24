package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionary;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class AllowlistMiddleware extends MessageMiddleware {
    private final AllowlistDictionary allowlistDictionary;

    public AllowlistMiddleware(final AllowlistDictionary allowlistDictionary) {
        this.allowlistDictionary = allowlistDictionary;
    }

    @Override
    protected Message handleInternal(final Message message) throws IOException {
        List<Record> questions = message.getQuestionRecords();
        for (Record question : questions) {
            String hostname = question.getName().getName();
            if (!allowlistDictionary.isAllowed(hostname)) {
                log.debug("Domain {} is not allowlisted", hostname);
                message.setBlocked();
                break;
            }
        }
        return handleNext(message);
    }

    @Override
    protected boolean shouldSkipMiddleware(final Message msg) {
        return msg.isQueryComplete();
    }
}
