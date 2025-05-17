package com.svincent7.dnsproxy.service.message;

import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

@Slf4j
public class MessageHandlerImpl implements MessageHandler {

    private final InetSocketAddress socketAddress;
    private DatagramChannel channel;

    private static final int BUFFER_SIZE = 512;
    private static final int DNS_PORT = 512;

    public MessageHandlerImpl() {
        this.socketAddress = new InetSocketAddress("1.1.1.1", DNS_PORT);
    }

    @Override
    public Message handleMessage(final Message msg) {
        try {
            if (!msg.isQueryComplete()) {
                channel = DatagramChannel.open();
                channel.configureBlocking(true);
                channel.bind(new InetSocketAddress(0));
                MessageOutput request = new MessageOutput();
                msg.toByteResponse(request);
                log.info("rqs: {}", request.getData());
                channel.send(ByteBuffer.wrap(request.getData()), socketAddress);

                ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                SocketAddress remoteAddress = channel.receive(receiveBuffer);

                if (remoteAddress == null) {
                    throw new RuntimeException("no remote address");
                }

                receiveBuffer.flip();
                byte[] response = new byte[receiveBuffer.remaining()];
                receiveBuffer.get(response);

                log.info("response: {}", response);
                return new Message(new MessageInput(response));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }
}
