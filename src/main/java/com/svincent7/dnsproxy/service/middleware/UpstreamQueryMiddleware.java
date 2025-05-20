package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.dnsclient.DNSClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class UpstreamQueryMiddleware extends MessageMiddleware {

    private final DNSClient dnsClient;

    public UpstreamQueryMiddleware(final DNSClient dnsClient) {
        this.dnsClient = dnsClient;
    }

    @Override
    public Message handle(final Message msg) throws IOException {
        MessageOutput request = new MessageOutput();
        msg.toByteResponse(request, dnsClient.getMaxPacketSize());
        byte[] response = dnsClient.send(request.getData());
        Message responseMessage = new Message(new MessageInput(response));
        return handleNext(responseMessage);
    }
}
