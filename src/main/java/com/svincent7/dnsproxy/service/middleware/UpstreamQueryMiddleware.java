package com.svincent7.dnsproxy.service.middleware;

import com.svincent7.dnsproxy.model.Message;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.service.dnsclient.DNSUDPClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpstreamQueryMiddleware extends MessageMiddleware {

    private final DNSUDPClient dnsudpClient;

    public UpstreamQueryMiddleware(final DNSUDPClient dnsudpClient) {
        this.dnsudpClient = dnsudpClient;
    }

    @Override
    public Message handle(final Message msg) {
        try {
            MessageOutput request = new MessageOutput();
            msg.toByteResponse(request);
            byte[] response = dnsudpClient.send(request.getData());
            Message responseMessage = new Message(new MessageInput(response));
            return handleNext(responseMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handleNext(msg);
    }
}
