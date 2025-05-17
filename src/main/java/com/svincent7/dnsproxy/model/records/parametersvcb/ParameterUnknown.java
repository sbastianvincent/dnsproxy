package com.svincent7.dnsproxy.model.records.parametersvcb;

import lombok.Getter;

@Getter
public class ParameterUnknown implements ParameterSvcBinding {
    private final int key;
    private byte[] value;

    public ParameterUnknown(final short key) {
        super();
        this.key = key;
    }

    @Override
    public byte[] toByteArr() {
        return value;
    }

    @Override
    public void fromByteArray(final byte[] bytes) {
        value = bytes;
    }
}
