package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;

public class QRecord extends Record {

    public QRecord(final Name name, final Type type, final DNSClass dnsClass) {
        super(name, type, dnsClass);
    }

    @Override
    public void toByteResponse(final MessageOutput messageOutput) {
        name.toByteResponse(messageOutput);
        messageOutput.writeU16(type.getValue());
        messageOutput.writeU16(dnsClass.getValue());
    }

    @Override
    protected void rrToByteResponse(final MessageOutput messageOutput) {

    }
}
