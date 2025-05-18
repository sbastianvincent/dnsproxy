package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.dnsclient.DNSUDPClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpstreamQueryMiddleware extends MessageMiddleware {

    private final CacheService cacheService;
    private final DNSUDPClient client;

    public UpstreamQueryMiddleware(final CacheService cacheService, final DNSUDPClient client) {
        this.cacheService = cacheService;
        this.client = client;
    }

    @Override
    public Message handle(final Message msg) {
        try {
            MessageOutput request = new MessageOutput();
            msg.toByteResponse(request);
            log.info("rqs: {}", request.getData());
            byte[] response = client.send(request.getData());
            log.info("response: {}", response);
            Message responseMessage = new Message(new MessageInput(response));
            cacheService.cacheResponse(responseMessage);
            return handleNext(responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handleNext(msg);
    }
}
