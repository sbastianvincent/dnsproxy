package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.model.Type;
import com.svincent7.dnsproxy.model.records.Record;

import java.util.Set;

public interface CacheService {
    Set<Type> getAllowlistedCacheTypes();
    DNSCacheEntry getCachedResponse(Record question);
    void cacheResponse(Record question, DNSCacheEntry response);
}
