package com.svincent7.dnsproxy.service.cache;

import com.svincent7.dnsproxy.model.Message;

public class NoopCacheService implements CacheService {

    @Override
    public Message getCachedResponse(final Message message) {
        return null;
    }

    @Override
    public void cacheResponse(final Message message) {

    }
}
