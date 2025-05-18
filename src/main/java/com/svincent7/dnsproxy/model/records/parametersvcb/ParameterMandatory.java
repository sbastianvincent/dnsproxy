package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ParameterMandatory implements ParameterSvcBinding {
    private final List<Short> values;

    public ParameterMandatory() {
        super();
        this.values = new ArrayList<>();
    }

    public ParameterMandatory(final ParameterMandatory parameterMandatory) {
        super();
        this.values = new ArrayList<>();
        this.values.addAll(parameterMandatory.getValues());
    }

    @Override
    public byte[] toByteArr() {
        MessageOutput message = new MessageOutput();
        for (Short value : values) {
            message.writeU16(value);
        }
        return message.toByteArray();
    }

    @Override
    public void fromByteArray(final byte[] bytes) throws DNSMessageParseException {
        MessageInput message = new MessageInput(bytes);
        while (message.remaining() >= 2) {
            short key = message.readU16();
            values.add(key);
        }
        if (message.remaining() > 0) {
            throw new DNSMessageParseException("Unexpected number of values in mandatory parameter");
        }
    }

    @Override
    public ParameterSvcBinding clone() {
        return new ParameterMandatory(this);
    }
}
