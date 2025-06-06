package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import lombok.Getter;

@Getter
public class ParameterPort implements ParameterSvcBinding {
    private int port;

    public ParameterPort() {
        super();
    }

    public ParameterPort(final ParameterPort port) {
        super();
        this.port = port.getPort();
    }

    @Override
    public byte[] toByteArr() {
        MessageOutput messageOutput = new MessageOutput();
        messageOutput.writeU16(port);
        return messageOutput.toByteArray();
    }

    @Override
    public void fromByteArray(final byte[] bytes) throws DNSMessageParseException {
        MessageInput message = new MessageInput(bytes);
        port = message.readU16();
        if (message.remaining() > 0) {
            throw new DNSMessageParseException("Unexpected number of bytes in port parameter");
        }
    }

    @Override
    public ParameterSvcBinding clone() {
        return new ParameterPort(this);
    }
}
