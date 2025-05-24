package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.blocklist.BlocklistDictionary;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class BlocklistMiddleware extends MessageMiddleware {
    private final BlocklistDictionary blocklistDictionary;

    public BlocklistMiddleware(final BlocklistDictionary blocklistDictionary) {
        this.blocklistDictionary = blocklistDictionary;
    }

    @Override
    protected Message handleInternal(final Message message) throws IOException {
        List<Record> questions = message.getQuestionRecords();
        for (Record question : questions) {
            String hostname = question.getName().getName();
            log.debug("Checking hostname: {}", hostname);
            if (blocklistDictionary.isBlocked(hostname)) {
                log.debug("Domain {} is blocked", hostname);
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
