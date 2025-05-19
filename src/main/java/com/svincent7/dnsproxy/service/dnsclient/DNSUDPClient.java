package com.svincent7.dnsproxy.service.dnsclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class DNSUDPClient implements DNSClient {
    private final InetSocketAddress address;

    private static final int BUFFER_SIZE = 512;
    private static final int DEFAULT_TIMEOUT = 5000;

    public DNSUDPClient(final String ip, final int port) {
        this.address = new InetSocketAddress(ip, port);
    }

    @Override
    public byte[] send(final byte[] data) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(0)); // bind to any port

            // Send the packet
            channel.send(ByteBuffer.wrap(data), address);

            // Setup selector for read timeout
            try (Selector selector = Selector.open()) {
                channel.register(selector, SelectionKey.OP_READ);

                int readyChannels = selector.select(DEFAULT_TIMEOUT);
                if (readyChannels == 0) {
                    throw new IOException("Timeout waiting for DNS response");
                }

                ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                SocketAddress remoteAddress = channel.receive(receiveBuffer);

                if (remoteAddress == null) {
                    throw new IOException("No response received from DNS server");
                }

                receiveBuffer.flip();
                byte[] response = new byte[receiveBuffer.remaining()];
                receiveBuffer.get(response);
                return response;
            }
        }
    }
}
