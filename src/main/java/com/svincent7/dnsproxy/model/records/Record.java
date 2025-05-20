package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Example.
 * ab cd 81 80 00 01 00 01 00 00 00 00
 * 03 77 77 77 07 65 78 61 6d 70 6c 65
 * 03 63 6f 6d 00 00 01 00 01
 * c0 0c 00 01 00 01 00 00 00 3c 00 04
 * 5d b8 d8 22
 * Breakdown:
 * First 12 bytes is Header
 * Next is Question Section
 * QNAME            03 77 77 77                'www' (length 3)
 *                  07 65 78 61 6d 70 6c 65    'example' (length 7)
 *                  03 63 6f 6d                'com' (length 3)
 *                  00                         End of domain
 * QTYPE            00 01                      Type A (IPv4 address)
 * QCLASS           00 01                      Class IN (Internet)
 * Answer NAME      c0 0c                      Pointer to 0x0c (QNAME)
 * TYPE             00 01                      A
 * CLASS            00 01                      IN
 * TTL              00 00 00 3c                60 seconds
 * RDLENGTH         00 04                      4 bytes
 * RDATA            5d b8 d8 22                IP: 93.184.216.34
 */
@Getter
@Slf4j
@ToString
@NoArgsConstructor
public abstract class Record implements Cloneable {
    protected Name name;
    protected Type type;
    protected DNSClass dnsClass;
    protected int length;
    protected long ttl;

    public Record(final Name name, final Type type, final DNSClass dnsClass) {
        this(name, type, dnsClass, 0, 0);
    }

    public Record(final Name name, final Type type, final DNSClass dnsClass, final long ttl, final int length) {
        this.name = name;
        this.type = type;
        this.dnsClass = dnsClass;
        this.length = length;
        this.ttl = ttl;
    }

    public int toByteResponse(final MessageOutput messageOutput, final int maxPacketSize) {
        int current = messageOutput.getPos();
        name.toByteResponse(messageOutput);
        messageOutput.writeU16(type.getValue());
        messageOutput.writeU16(dnsClass.getValue());
        messageOutput.writeU32(ttl);
        int lengthPosition = messageOutput.getPos();
        messageOutput.writeU16((short) 0);
        rrToByteResponse(messageOutput);
        int rrLength = messageOutput.getPos() - lengthPosition - 2;
        messageOutput.writeU16At(rrLength, lengthPosition);
        int packetSize = messageOutput.getPos() - current;
        if (messageOutput.getPos() > maxPacketSize) {
            messageOutput.setPos(current);
        }
        return packetSize;
    }

    protected abstract void rrToByteResponse(MessageOutput messageOutput);

    @Override
    public Record clone() {
        try {
            return (Record) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cloning failed", e);
        }
    }
}
