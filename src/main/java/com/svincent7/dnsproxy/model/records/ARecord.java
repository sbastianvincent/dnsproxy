package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import lombok.Getter;
import lombok.ToString;

/**
 * https://datatracker.ietf.org/doc/html/rfc1035#section-3.4.1.
 * ab cd 81 80 00 01 00 01 00 00 00 00                      ; Header
 * 03 77 77 77                                              ; 'www'
 * 07 65 78 61 6d 70 6c 65                                  ; 'example'
 * 03 63 6f 6d                                              ; 'com'
 * 00                                                       ; End of QNAME
 * 00 01                                                    ; QTYPE = 1 (A)
 * 00 01                                                    ; QCLASS = IN
 * c0 0c                                                    ; NAME = pointer to offset 0x0c
 * 00 01                                                    ; TYPE = 1 (A)
 * 00 01                                                    ; CLASS = IN
 * 00 00 00 3c                                              ; TTL = 60 seconds
 * 00 04                                                    ; RDLENGTH = 4 bytes
 * 5d b8 d8 22                                              ; RDATA = 93.184.216.34
 */
@Getter
@ToString(callSuper = true)
public class ARecord extends Record {
    private final int addr;
    private final String ipAddress;

    private static final int IPV4_LENGTH = 4;
    private static final int UNSIGNED_BYTE_MASK = 0xFF;
    private static final long UNSIGNED_INT_MASK = 0xFFFFFFFFL;

    private static final int SHIFT_24 = 24;
    private static final int SHIFT_16 = 16;
    private static final int SHIFT_8 = 8;

    private static final int IP_INDEX_0 = 0;
    private static final int IP_INDEX_1 = IP_INDEX_0 + 1;
    private static final int IP_INDEX_2 = IP_INDEX_1 + 1;
    private static final int IP_INDEX_3 = IP_INDEX_2 + 1;

    private static final String IP_SEPARATOR = ".";

    public ARecord(final Name name, final Type type, final DNSClass dnsClass, final long ttl, final int length,
                   final MessageInput message) {
        super(name, type, dnsClass, ttl, length);
        this.addr = fromArray(message.readByteArray(IPV4_LENGTH));
        ipAddress = getIpAddress(toArray(addr));
    }

    public ARecord(final ARecord record) {
        super(record.getName().clone(), record.getType(), record.getDnsClass(), record.getTtl(), record.getLength());
        this.addr = record.getAddr();
        this.ipAddress = record.getIpAddress();
    }

    public ARecord(final String domainName, final long ttl, final String ipAddress) {
        super(new Name(domainName), Type.A, DNSClass.IN, ttl, IPV4_LENGTH);
        this.ipAddress = ipAddress;
        this.addr = ipToInt(ipAddress);
    }

    private static int fromArray(final byte[] array) {
        return ((array[IP_INDEX_0] & UNSIGNED_BYTE_MASK) << SHIFT_24)
                | ((array[IP_INDEX_1] & UNSIGNED_BYTE_MASK) << SHIFT_16)
                | ((array[IP_INDEX_2] & UNSIGNED_BYTE_MASK) << SHIFT_8)
                | (array[IP_INDEX_3] & UNSIGNED_BYTE_MASK);
    }

    private static byte[] toArray(final int addr) {
        return new byte[] {
                (byte) ((addr >>> SHIFT_24) & UNSIGNED_BYTE_MASK),
                (byte) ((addr >>> SHIFT_16) & UNSIGNED_BYTE_MASK),
                (byte) ((addr >>> SHIFT_8) & UNSIGNED_BYTE_MASK),
                (byte) (addr & UNSIGNED_BYTE_MASK)
        };
    }

    @Override
    protected void rrToByteResponse(final MessageOutput messageOutput) {
        messageOutput.writeU32(addr & UNSIGNED_INT_MASK);
    }

    @Override
    public Record clone() {
        return new ARecord(this);
    }

    private int ipToInt(final String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != IPV4_LENGTH) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ip);
        }

        return (Integer.parseInt(parts[IP_INDEX_0]) << SHIFT_24)
                | (Integer.parseInt(parts[IP_INDEX_1]) << SHIFT_16)
                | (Integer.parseInt(parts[IP_INDEX_2]) << SHIFT_8)
                | Integer.parseInt(parts[IP_INDEX_3]);
    }

    private String getIpAddress(final byte[] bytes) {
            return (bytes[IP_INDEX_0] & UNSIGNED_BYTE_MASK)
                    + IP_SEPARATOR
                    + (bytes[IP_INDEX_1] & UNSIGNED_BYTE_MASK)
                    + IP_SEPARATOR
                    + (bytes[IP_INDEX_2] & UNSIGNED_BYTE_MASK)
                    + IP_SEPARATOR
                    + (bytes[IP_INDEX_3] & UNSIGNED_BYTE_MASK);
    }
}
