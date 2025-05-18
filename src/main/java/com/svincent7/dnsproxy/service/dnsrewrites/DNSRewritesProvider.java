package com.svincent7.dnsproxy.service.dnsrewrites;

import com.svincent7.dnsproxy.model.records.QRecord;

public interface DNSRewritesProvider {
    DNSRewrites getDNSRewrites(QRecord record);
}
