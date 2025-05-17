package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import lombok.Getter;
import lombok.ToString;

/**
 * https://datatracker.ietf.org/doc/html/rfc1035#section-3.3.13.
 * ab cd 81 80 00 01 00 01 00 00 00 00                   ; Header
 * 07 101 120 97 109 112 108 101                         ; 'example'
 * 03 99 111 109                                         ; 'com'
 * 00                                                    ; End of QNAME
 * 00 06                                                 ; QTYPE = 6 (SOA)
 * 00 01                                                 ; QCLASS = IN
 * c0 0c                                                 ; NAME = pointer to QNAME
 * 00 06                                                 ; TYPE = 6 (SOA)
 * 00 01                                                 ; CLASS = IN
 * 00 00 00 3c                                           ; TTL = 60 seconds
 * 00 2c                                                 ; RDLENGTH = 44 bytes
 * 03 110 115 49                                         ; Primary NS = 'ns1'
 * 07 101 120 97 109 112 108 101                         ; 'example'
 * 03 99 111 109                                         ; 'com'
 * 00                                                    ; End
 * 08 104 111 115 116 109 97 115 116                     ; RNAME = 'hostmast'
 * 03 101 120 97                                         ; 'exa'
 * 03 109 112 108                                        ; 'mpl'
 * 03 101 99 111 109                                     ; 'ecom'
 * 00                                                    ; End
 * 00 00 00 01                                           ; SERIAL = 1
 * 00 00 0e 10                                           ; REFRESH = 3600
 * 00 00 01 2c                                           ; RETRY = 300
 * 00 00 2a 30                                           ; EXPIRE = 10800
 * 00 00 07 08                                           ; MINIMUM = 1800
 */
@Getter
@ToString
public class SOARecord extends Record {
    private final Name host;
    private final Name admin;
    private final long serial;
    private final long refresh;
    private final long retry;
    private final long expire;
    private final long minimum;

    public SOARecord(final Name name, final Type type, final DNSClass dnsClass, final long ttl, final int length,
                     final MessageInput message) {
        super(name, type, dnsClass, ttl, length);
        this.host = new Name(message);
        this.admin = new Name(message);
        this.serial = message.readU32();
        this.refresh = message.readU32();
        this.retry = message.readU32();
        this.expire = message.readU32();
        this.minimum = message.readU32();
    }

    @Override
    protected void rrToByteResponse(final MessageOutput messageOutput) {
        name.toByteResponse(messageOutput);
        admin.toByteResponse(messageOutput);
        messageOutput.writeU32(serial);
        messageOutput.writeU32(refresh);
        messageOutput.writeU32(retry);
        messageOutput.writeU32(expire);
        messageOutput.writeU32(minimum);
    }
}
