package com.svincent7.dnsproxy.service.packet;

import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.message.MessageHandler;
import com.svincent7.dnsproxy.service.message.MessageHandlerImpl;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Slf4j
public class UDPHandler implements PacketHandler {
    private final DatagramSocket socket;
    private final DatagramPacket packet;
    private final MessageHandler messageHandler;

    public UDPHandler(final DatagramSocket socket, final DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
        this.messageHandler = new MessageHandlerImpl();
    }

    @Override
    public void handlePacket() throws Exception {
        log.debug("Pkt: {}", packet.getData());
        final MessageInput msg = new MessageInput(packet.getData());
        final Message message = new Message(msg);
        Message responseMessage = messageHandler.handleMessage(message);
        MessageOutput request = new MessageOutput();
        responseMessage.toByteResponse(request);
        log.debug("Reply: {}", request.getData());
        DatagramPacket reply = new DatagramPacket(
                request.getData(),
                request.getData().length,
                packet.getAddress(),
                packet.getPort()
        );
        socket.send(reply);
    }
}
