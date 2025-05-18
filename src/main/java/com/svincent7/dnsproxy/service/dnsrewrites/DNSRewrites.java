package com.svincent7.dnsproxy.service.dnsrewrites;


import com.svincent7.dnsproxy.model.records.QRecord;
import lombok.Data;

@Data
public class DNSRewrites {
    public static final int DEFAULT_REWRITE_TTL = 60;

    private QRecord record;
    private String answer;

    public DNSRewrites(final QRecord record, final String answer) {
        this.record = record;
        this.answer = answer;
    }
}
