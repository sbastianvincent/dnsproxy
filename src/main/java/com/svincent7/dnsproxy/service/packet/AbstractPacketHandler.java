package com.svincent7.dnsproxy.service.packet;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.dnsclient.DNSClient;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import com.svincent7.dnsproxy.service.middleware.CacheAnswerMiddleware;
import com.svincent7.dnsproxy.service.middleware.CacheLookupMiddleware;
import com.svincent7.dnsproxy.service.middleware.DNSRewritesMiddleware;
import com.svincent7.dnsproxy.service.middleware.MessageMiddleware;
import com.svincent7.dnsproxy.service.middleware.UpstreamQueryMiddleware;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class AbstractPacketHandler implements PacketHandler {
    private final MessageMiddleware middleware;

    public AbstractPacketHandler(final DNSRewritesProvider dnsRewritesProvider, final CacheService cacheService,
                                 final DNSClient dnsClient) {
        this.middleware = MessageMiddleware.link(
                new DNSRewritesMiddleware(dnsRewritesProvider),
                new CacheLookupMiddleware(cacheService),
                new UpstreamQueryMiddleware(dnsClient),
                new CacheAnswerMiddleware(cacheService)
        );
    }

    @Override
    public void handlePacket() throws IOException {
        long startTime = System.currentTimeMillis();
        final Message message = getMessageFromInput();

        Message responseMessage = middleware.handle(message);

        MessageOutput request = new MessageOutput();
        responseMessage.toByteResponse(request, getMaxPacketSize());
        log.debug("Response: {}", request);

        sendResponse(request);
        long endTime = System.currentTimeMillis();
        log.debug("DNS response {} sent in {}ms", responseMessage, endTime - startTime);
    }

    protected abstract int getMaxPacketSize();
    protected abstract Message getMessageFromInput() throws IOException;
    protected abstract void sendResponse(MessageOutput messageOutput) throws IOException;
}
