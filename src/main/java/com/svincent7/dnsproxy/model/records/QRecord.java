package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;

public class QRecord extends Record {

    public QRecord(final Name name, final Type type, final DNSClass dnsClass) {
        super(name, type, dnsClass);
    }

    public QRecord(final QRecord qRecord) {
        super(qRecord.getName().clone(), qRecord.getType(), qRecord.getDnsClass());
    }

    @Override
    public int toByteResponse(final MessageOutput messageOutput, final int maxPacketSize) {
        int current = messageOutput.getPos();
        name.toByteResponse(messageOutput);
        messageOutput.writeU16(type.getValue());
        messageOutput.writeU16(dnsClass.getValue());
        int packetSize = messageOutput.getPos() - current;
        if (messageOutput.getPos() > maxPacketSize) {
            messageOutput.setPos(current);
        }
        return packetSize;
    }

    @Override
    protected void rrToByteResponse(final MessageOutput messageOutput) {

    }

    @Override
    public Record clone() {
        return new QRecord(this);
    }
}
