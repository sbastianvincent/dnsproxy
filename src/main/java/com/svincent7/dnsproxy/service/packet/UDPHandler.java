package com.svincent7.dnsproxy.service.packet;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.dnsclient.DNSUDPClient;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import com.svincent7.dnsproxy.service.middleware.CacheLookupMiddleware;
import com.svincent7.dnsproxy.service.middleware.DNSRewritesMiddleware;
import com.svincent7.dnsproxy.service.middleware.MessageMiddleware;
import com.svincent7.dnsproxy.service.middleware.UpstreamQueryMiddleware;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Slf4j
public class UDPHandler implements PacketHandler {
    private final DatagramSocket socket;
    private final DatagramPacket packet;
    private final MessageMiddleware middleware;

    public UDPHandler(final DatagramSocket socket, final DatagramPacket packet, final CacheService cacheService,
                      final DNSUDPClient client, final DNSRewritesProvider dnsRewritesProvider) {
        this.socket = socket;
        this.packet = packet;
        this.middleware = MessageMiddleware.link(
                new DNSRewritesMiddleware(dnsRewritesProvider),
                new CacheLookupMiddleware(cacheService),
                new UpstreamQueryMiddleware(cacheService, client)
        );
    }

    @Override
    public void handlePacket() throws Exception {
        log.debug("Pkt: {}", packet.getData());
        final MessageInput messageInput = new MessageInput(packet.getData());
        final Message message = new Message(messageInput);

        Message responseMessage = middleware.handle(message);

        MessageOutput request = new MessageOutput();
        responseMessage.toByteResponse(request);
        log.debug("Reply: {}", request.getData());
        log.debug("Reply Message: {}", new Message(new MessageInput(request.getData())));
        DatagramPacket reply = new DatagramPacket(
                request.getData(),
                request.getData().length,
                packet.getAddress(),
                packet.getPort()
        );
        socket.send(reply);
    }
}
