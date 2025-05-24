package com.svincent7.dnsproxy.service.packet;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
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
import java.net.Socket;

@Slf4j
public class TCPHandler extends AbstractPacketHandler {
    private final Socket socket;

    private static final int MAX_PACKET_SIZE = 65536;
    private static final int UNSIGNED_BYTE_MASK = 0xFF;
    private static final int SHIFT_8 = 8;

    public TCPHandler(final BlocklistDictionary blocklistDictionary,
                      final AllowlistDictionary allowlistDictionary,
                      final Socket socket,
                      final CacheService cacheService,
                      final DNSResolverFactory dnsResolverFactory,
                      final DNSRewritesProvider dnsRewritesProvider) {
        super(blocklistDictionary, allowlistDictionary, dnsRewritesProvider, cacheService, dnsResolverFactory);
        this.socket = socket;
    }

    @Override
    protected int getMaxPacketSize() {
        return MAX_PACKET_SIZE;
    }

    @Override
    protected Message getMessageFromInput() throws IOException {
        var input = socket.getInputStream();
        byte[] lengthBytes = input.readNBytes(2);
        if (lengthBytes.length < 2) {
            throw new DNSMessageParseException("Invalid TCP request: insufficient length");
        }

        int length = ((lengthBytes[0] & UNSIGNED_BYTE_MASK) << SHIFT_8) | (lengthBytes[1] & UNSIGNED_BYTE_MASK);
        final MessageInput messageInput = new MessageInput(input.readNBytes(length));
        return new Message(messageInput);
    }

    @Override
    protected void sendResponse(final MessageOutput messageOutput) throws IOException {
        var output = socket.getOutputStream();
        var response = messageOutput.getData();
        output.write((response.length >> SHIFT_8) & UNSIGNED_BYTE_MASK);
        output.write(response.length & UNSIGNED_BYTE_MASK);
        output.write(response);
        output.flush();
    }
}
