package com.svincent7.dnsproxy.service.packet;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.alllowlist.AllowlistDictionary;
import com.svincent7.dnsproxy.service.blocklist.BlocklistDictionary;
import com.svincent7.dnsproxy.service.cache.CacheService;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewritesProvider;
import com.svincent7.dnsproxy.service.resolver.DNSResolverFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Slf4j
public class UDPHandler extends AbstractPacketHandler {
    private final DatagramSocket socket;
    private final DatagramPacket packet;

    private static final int MAX_PACKET_SIZE = 512;

    public UDPHandler(final BlocklistDictionary blocklistDictionary,
                      final AllowlistDictionary allowlistDictionary,
                      final DatagramSocket socket,
                      final DatagramPacket packet,
                      final CacheService cacheService,
                      final DNSResolverFactory dnsResolverFactory,
                      final DNSRewritesProvider dnsRewritesProvider) {
        super(blocklistDictionary, allowlistDictionary, dnsRewritesProvider, cacheService, dnsResolverFactory);
        this.socket = socket;
        this.packet = packet;
    }

    @Override
    protected int getMaxPacketSize() {
        return MAX_PACKET_SIZE;
    }

    @Override
    protected Message getMessageFromInput() throws IOException {
        final MessageInput messageInput = new MessageInput(packet.getData());
        return new Message(messageInput);
    }

    @Override
    protected void sendResponse(final MessageOutput messageOutput) throws IOException {
        DatagramPacket reply = new DatagramPacket(
                messageOutput.getData(),
                messageOutput.getData().length,
                packet.getAddress(),
                packet.getPort()
        );
        socket.send(reply);
    }
}
