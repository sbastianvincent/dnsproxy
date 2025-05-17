package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import lombok.Getter;

@Getter
public class ParameterNoDefaultAlpn implements ParameterSvcBinding {
    public ParameterNoDefaultAlpn() {
        super();
    }

    @Override
    public byte[] toByteArr() {
        return new byte[0];
    }

    @Override
    public void fromByteArray(final byte[] bytes) throws DNSMessageParseException {
        if (bytes.length > 0) {
            throw new DNSMessageParseException("No value should be specified for no default alpn");
        }
    }
}
