package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParameterIpv4Hint implements ParameterSvcBinding {
    private final List<byte[]> addresses;

    private static final int IPV4_ADDRESS_LENGTH = 4;

    public ParameterIpv4Hint() {
        super();
        this.addresses = new ArrayList<>();
    }

    @Override
    public byte[] toByteArr() {
        MessageOutput messageOutput = new MessageOutput();
        for (byte[] b : addresses) {
            messageOutput.writeByteArray(b);
        }
        return messageOutput.toByteArray();
    }

    @Override
    public void fromByteArray(final byte[] bytes) throws DNSMessageParseException {
        MessageInput messageInput = new MessageInput(bytes);
        while (messageInput.remaining() >= IPV4_ADDRESS_LENGTH) {
            addresses.add(messageInput.readByteArray(IPV4_ADDRESS_LENGTH));
        }
        if (messageInput.remaining() > 0) {
            throw new DNSMessageParseException("Unexpected number of bytes in ipv4hint parameter");
        }
    }
}
