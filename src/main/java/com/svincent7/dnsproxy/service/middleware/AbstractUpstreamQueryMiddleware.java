package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import com.svincent7.dnsproxy.service.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class AbstractUpstreamQueryMiddleware extends MessageMiddleware {

    private final DNSResolverFactory resolverFactory;

    public AbstractUpstreamQueryMiddleware(final DNSResolverFactory resolverFactory) {
        this.resolverFactory = resolverFactory;
    }

    @Override
    protected Message handleInternal(final Message msg) throws IOException {
        Resolver resolver = getResolver(resolverFactory);
        MessageOutput request = new MessageOutput();
        msg.toByteResponse(request, resolver.getMaxPacketSize());
        log.debug("resolver: {} - request: {}", resolver.getAddress(), request);
        byte[] response = resolver.send(request.getData());
        Message responseMessage = new Message(new MessageInput(response));
        return handleNext(responseMessage);
    }

    protected abstract Resolver getResolver(DNSResolverFactory resolverFactory);
}
