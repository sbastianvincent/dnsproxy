package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Header;
import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.records.QRecord;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewrites;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class DNSRewritesMiddleware extends MessageMiddleware {

    private final DNSRewritesProvider dnsRewritesProvider;

    public DNSRewritesMiddleware(final DNSRewritesProvider dnsRewritesProvider) {
        this.dnsRewritesProvider = dnsRewritesProvider;
    }

    @Override
    public Message handle(final Message msg) {
        List<Record> records = msg.getSections().get(Header.SECTION_QUESTION);
        for (Record record : records) {
            if (record instanceof QRecord) {
                DNSRewrites dnsRewrites = dnsRewritesProvider.getDNSRewrites((QRecord) record);
                if (dnsRewrites == null) {
                    continue;
                }

                Message response = Message.fromDNSRewrites(msg, dnsRewrites);
                return handleNext(response);
            }
        }
        return handleNext(msg);
    }
}
