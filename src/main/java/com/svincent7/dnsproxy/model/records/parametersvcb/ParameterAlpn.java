package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParameterAlpn implements ParameterSvcBinding, Cloneable {
    private final List<byte[]> values;

    public ParameterAlpn() {
        super();
        this.values = new ArrayList<>();
    }

    public ParameterAlpn(final ParameterAlpn parameterAlpn) {
        super();
        this.values = new ArrayList<>();
        for (byte[] value : parameterAlpn.values) {
            this.values.add(value.clone());
        }
    }

    @Override
    public byte[] toByteArr() {
        MessageOutput messageOutput = new MessageOutput();
        for (byte[] value : values) {
            messageOutput.writeCountedString(value);
        }
        return messageOutput.toByteArray();
    }

    @Override
    public void fromByteArray(final byte[] bytes) {
        MessageInput message = new MessageInput(bytes);
        while (message.remaining() > 0) {
            byte[] b = message.readCountedString();
            values.add(b);
        }
    }

    @Override
    public ParameterAlpn clone() {
        return new ParameterAlpn(this);
    }
}
