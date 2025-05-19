package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.model.records.Record;
import lombok.Data;

@Data
public class DNSCacheEntry {
    private Record answer;
    private long expiredTime;
}
