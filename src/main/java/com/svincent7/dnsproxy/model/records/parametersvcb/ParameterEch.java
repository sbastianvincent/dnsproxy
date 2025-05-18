package com.svincent7.dnsproxy.model.records.parametersvcb;

import lombok.Getter;

@Getter
public class ParameterEch implements ParameterSvcBinding {
    private byte[] data;

    public ParameterEch() {
        super();
    }

    public ParameterEch(final ParameterEch parameterEch) {
        super();
        this.data = parameterEch.data.clone();
    }

    @Override
    public byte[] toByteArr() {
        return data;
    }

    @Override
    public void fromByteArray(final byte[] bytes) {
        data = bytes;
    }

    @Override
    public ParameterSvcBinding clone() {
        return new ParameterEch(this);
    }
}
