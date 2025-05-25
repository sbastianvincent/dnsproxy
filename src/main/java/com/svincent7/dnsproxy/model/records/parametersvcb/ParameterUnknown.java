package com.svincent7.dnsproxy.model.records.parametersvcb;

import lombok.Getter;

@Getter
public class ParameterUnknown implements ParameterSvcBinding {
    private final int key;
    private byte[] value;

    public ParameterUnknown(final int key) {
        super();
        this.key = key;
    }

    public ParameterUnknown(final ParameterUnknown parameter) {
        super();
        this.key = parameter.getKey();
        this.value = parameter.getValue().clone();
    }

    @Override
    public byte[] toByteArr() {
        return value;
    }

    @Override
    public void fromByteArray(final byte[] bytes) {
        value = bytes;
    }

    @Override
    public ParameterSvcBinding clone() {
        return new ParameterUnknown(this);
    }
}
