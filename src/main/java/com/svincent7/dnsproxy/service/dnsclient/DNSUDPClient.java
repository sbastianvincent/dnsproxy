package com.svincent7.dnsproxy.service.dnsclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class DNSUDPClient implements DNSClient {
    private final InetSocketAddress address;

    private static final int BUFFER_SIZE = 512;

    public DNSUDPClient(final String ip, final int port) {
        this.address = new InetSocketAddress(ip, port);
    }

    @Override
    public byte[] send(byte[] data) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(true);
            channel.bind(new InetSocketAddress(0));

            channel.send(ByteBuffer.wrap(data), address);

            ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            SocketAddress remoteAddress = channel.receive(receiveBuffer);

            if (remoteAddress == null) {
                throw new RuntimeException("no remote address");
            }

            receiveBuffer.flip();
            byte[] response = new byte[receiveBuffer.remaining()];
            receiveBuffer.get(response);
            return response;
        }
    }
}
