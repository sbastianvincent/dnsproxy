package com.svincent7.dnsproxy.service.resolver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TCPResolver implements Resolver {
    private final InetSocketAddress address;

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int DNS_TCP_PACKET_SIZE = 65535;

    public TCPResolver(final String ip, final int port) {
        this.address = new InetSocketAddress(ip, port);
    }

    @Override
    public byte[] send(final byte[] data) throws IOException {
        try (SocketChannel channel = SocketChannel.open()) {
            channel.configureBlocking(false);
            channel.connect(address);

            try (Selector selector = Selector.open()) {
                channel.register(selector, SelectionKey.OP_CONNECT);

                if (selector.select(DEFAULT_TIMEOUT) == 0) {
                    throw new IOException("Timeout connecting to DNS server");
                }

                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isConnectable()) {
                        if (!channel.finishConnect()) {
                            throw new IOException("Could not finish TCP connection");
                        }
                    }
                }

                // Add 2-byte length prefix for TCP
                ByteBuffer sendBuffer = ByteBuffer.allocate(2 + data.length);
                sendBuffer.putShort((short) data.length);
                sendBuffer.put(data);
                sendBuffer.flip();
                while (sendBuffer.hasRemaining()) {
                    channel.write(sendBuffer);
                }

                // Prepare to read response
                selector.selectedKeys().clear();
                channel.register(selector, SelectionKey.OP_READ);

                if (selector.select(DEFAULT_TIMEOUT) == 0) {
                    throw new IOException("Timeout waiting for DNS response");
                }

                // Read 2-byte length prefix
                ByteBuffer lengthBuffer = ByteBuffer.allocate(2);
                while (lengthBuffer.hasRemaining()) {
                    if (channel.read(lengthBuffer) < 0) {
                        throw new IOException("Connection closed before reading length.");
                    }
                }
                lengthBuffer.flip();
                int responseLength = lengthBuffer.getShort() & 0xFFFF;

                // Read the DNS response
                ByteBuffer receiveBuffer = ByteBuffer.allocate(responseLength);
                while (receiveBuffer.hasRemaining()) {
                    if (channel.read(receiveBuffer) < 0) {
                        throw new IOException("Connection closed while reading DNS response");
                    }
                }

                receiveBuffer.flip();
                byte[] response = new byte[receiveBuffer.remaining()];
                receiveBuffer.get(response);
                return response;
            }
        }
    }

    @Override
    public int getMaxPacketSize() {
        return DNS_TCP_PACKET_SIZE;
    }
}
