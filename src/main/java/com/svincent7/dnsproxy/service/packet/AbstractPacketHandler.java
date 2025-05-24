package com.svincent7.dnsproxy.service.packet;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.blocklist.BlocklistDictionary;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import com.svincent7.dnsproxy.service.middleware.BlocklistMiddleware;
import com.svincent7.dnsproxy.service.middleware.CacheAnswerMiddleware;
import com.svincent7.dnsproxy.service.middleware.CacheLookupMiddleware;
import com.svincent7.dnsproxy.service.middleware.DNSRewritesMiddleware;
import com.svincent7.dnsproxy.service.middleware.MessageMiddleware;
import com.svincent7.dnsproxy.service.middleware.NoopMiddleware;
import com.svincent7.dnsproxy.service.middleware.UpstreamTCPQueryMiddleware;
import com.svincent7.dnsproxy.service.middleware.UpstreamUDPQueryMiddleware;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class AbstractPacketHandler implements PacketHandler {
    private final MessageMiddleware middleware;

    public AbstractPacketHandler(final BlocklistDictionary blocklistDictionary,
                                 final DNSRewritesProvider dnsRewritesProvider,
                                 final CacheService cacheService,
                                 final DNSResolverFactory dnsResolverFactory) {
        this.middleware = MessageMiddleware.link(
                new BlocklistMiddleware(blocklistDictionary),
                new DNSRewritesMiddleware(dnsRewritesProvider),
                new CacheLookupMiddleware(cacheService),
                getClass().getSimpleName().equals("UDPHandler")
                        ? new UpstreamUDPQueryMiddleware(dnsResolverFactory) : new NoopMiddleware(),
                new UpstreamTCPQueryMiddleware(dnsResolverFactory),
                new CacheAnswerMiddleware(cacheService)
        );
    }

    @Override
    public void handlePacket() throws IOException {
        long startTime = System.currentTimeMillis();
        final Message message = getMessageFromInput();
        log.debug("Request {}", message);

        Message responseMessage = middleware.handle(message);

        MessageOutput request = new MessageOutput();
        responseMessage.toByteResponse(request, getMaxPacketSize());

        sendResponse(request);
        long endTime = System.currentTimeMillis();
        log.debug("({}ms) DNS response {} - Byte: {}", endTime - startTime, responseMessage, request);
    }

    protected abstract int getMaxPacketSize();
    protected abstract Message getMessageFromInput() throws IOException;
    protected abstract void sendResponse(MessageOutput messageOutput) throws IOException;
}
