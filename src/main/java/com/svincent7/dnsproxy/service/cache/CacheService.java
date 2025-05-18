package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.model.Message;

public interface CacheService {
    Message getCachedResponse(Message message);
    void cacheResponse(Message message);
}
