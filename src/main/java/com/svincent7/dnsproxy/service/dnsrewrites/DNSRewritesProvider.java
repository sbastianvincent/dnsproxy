package com.svincent7.dnsproxy.service.dnsrewrites;

import com.svincent7.dnsproxy.model.records.Record;

import java.util.List;

public interface DNSRewritesProvider {
    List<Record> getDNSRewritesAnswer(Record question);
}
