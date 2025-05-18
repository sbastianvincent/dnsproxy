package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * https://datatracker.ietf.org/doc/html/rfc3596.
 * ab cd 81 80 00 01 00 01 00 00 00 00                      ; Header
 * 03 77 77 77                                              ; 'www'
 * 07 65 78 61 6d 70 6c 65                                  ; 'example'
 * 03 63 6f 6d                                              ; 'com'
 * 00                                                       ; End of QNAME
 * 00 1c                                                    ; QTYPE = 28 (AAAA)
 * 00 01                                                    ; QCLASS = IN
 * c0 0c                                                    ; NAME = pointer to offset 0x0c
 * 00 1c                                                    ; TYPE = 28 (AAAA)
 * 00 01                                                    ; CLASS = IN
 * 00 00 00 3c                                              ; TTL = 60 seconds
 * 00 10                                                    ; RDLENGTH = 16 bytes
 * 20 01 0d b8 00 00 00 00 00 00 00 00 00 00 00 01          ; RDATA = 2001:db8::1
 */
@Getter
@ToString(callSuper = true)
@Slf4j
public class AAAARecord extends Record {
    private final byte[] address;
    private final String ipAddress;

    private static final int IPV6_ADDRESS_LENGTH = 16;
    private static final int IPV4_ADDRESS_LENGTH = 4;
    private static final String IPV4_MAPPED_PREFIX = "::ffff";

    public AAAARecord(final Name name, final Type type, final DNSClass dnsClass, final long ttl, final int length,
                      final MessageInput message) {
        super(name, type, dnsClass, ttl, length);
        this.address = message.readByteArray(IPV6_ADDRESS_LENGTH);
        this.ipAddress = getIpAddress();
    }

    public AAAARecord(final AAAARecord aaaaRecord) {
        super(aaaaRecord.getName().clone(), aaaaRecord.getType(), aaaaRecord.getDnsClass(), aaaaRecord.getTtl(),
                aaaaRecord.getLength());
        this.address = aaaaRecord.getAddress().clone();
        this.ipAddress = aaaaRecord.getIpAddress();
    }

    private String getIpAddress() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByAddress(null, address);
        } catch (UnknownHostException e) {
            log.error("Unknown IP address", e);
            return null;
        }

        // IPV6
        if (inetAddress.getAddress().length == IPV4_ADDRESS_LENGTH) {
            return IPV4_MAPPED_PREFIX + inetAddress.getHostAddress();
        }
        return inetAddress.getHostAddress();
    }

    @Override
    protected void rrToByteResponse(final MessageOutput messageOutput) {
        messageOutput.writeByteArray(address);
    }

    @Override
    public Record clone() {
        return new AAAARecord(this);
    }
}
