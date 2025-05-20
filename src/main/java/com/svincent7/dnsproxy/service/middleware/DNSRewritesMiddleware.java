package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;


@Slf4j
public class DNSRewritesMiddleware extends MessageMiddleware {

    private final DNSRewritesProvider dnsRewritesProvider;

    public DNSRewritesMiddleware(final DNSRewritesProvider dnsRewritesProvider) {
        this.dnsRewritesProvider = dnsRewritesProvider;
    }

    @Override
    protected Message handleInternal(final Message msg) throws IOException {
        List<Record> records = msg.getQuestionRecords();
        for (Record question : records) {
            List<Record> dnsRewritesAnswer = dnsRewritesProvider.getDNSRewritesAnswer(question);
            if (dnsRewritesAnswer == null) {
                continue;
            }

            for (Record dnsRewrite : dnsRewritesAnswer) {
                log.debug("DNS Rewrites added answer: {}", dnsRewritesAnswer);
                msg.addAnswerRecord(dnsRewrite);
            }
            msg.setDNSRewritten(true);
        }
        return handleNext(msg);
    }

    @Override
    protected boolean shouldSkipMiddleware(final Message msg) {
        return msg.isQueryComplete();
    }
}
