package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import com.svincent7.dnsproxy.service.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpstreamUDPQueryMiddleware extends AbstractUpstreamQueryMiddleware {

    public UpstreamUDPQueryMiddleware(final DNSResolverFactory dnsResolverFactory) {
        super(dnsResolverFactory);
    }

    @Override
    protected Resolver getResolver(final DNSResolverFactory dnsResolverFactory) {
        return dnsResolverFactory.createUDPResolver();
    }

    @Override
    protected boolean shouldSkipMiddleware(final Message msg) {
        return msg.isQueryComplete();
    }
}
