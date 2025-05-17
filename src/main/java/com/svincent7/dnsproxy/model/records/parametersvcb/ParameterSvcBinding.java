package com.svincent7.dnsproxy.model.records.parametersvcb;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;

import java.io.Serializable;

public interface ParameterSvcBinding extends Serializable {

    byte[] toByteArr();
    void fromByteArray(byte[] bytes) throws DNSMessageParseException;
}
