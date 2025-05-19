package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.MessageOutput;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import lombok.Getter;
import lombok.ToString;

/**
 * https://datatracker.ietf.org/doc/html/rfc1035#section-3.3.1.
 * ab cd 81 80 00 01 00 01 00 00 00 00                      ; Header
 * 03 119 119 119                                           ; 'www'
 * 07 101 120 97 109 112 108 101                            ; 'example'
 * 03 99 111 109                                            ; 'com'
 * 00                                                       ; End of QNAME
 * 00 05                                                    ; QTYPE = 5 (CNAME)
 * 00 01                                                    ; QCLASS = IN
 * c0 0c                                                    ; NAME = pointer to QNAME
 * 00 05                                                    ; TYPE = 5 (CNAME)
 * 00 01                                                    ; CLASS = IN
 * 00 00 00 3c                                              ; TTL = 60 seconds
 * 00 0f                                                    ; RDLENGTH = 15 bytes
 * 03 119 119 119                                           ; 'www'
 * 06 103 111 111 103 108 101                               ; 'google'
 * 03 99 111 109                                            ; 'com'
 * 00                                                       ; End of name
 */
@Getter
@ToString(callSuper = true)
public class CNAMERecord extends Record {
    private final Name singleName;

    public CNAMERecord(final Name name, final Type type, final DNSClass dnsClass, final long ttl, final int length,
                       final MessageInput message) {
        super(name, type, dnsClass, ttl, length);
        singleName = new Name(message);
    }

    public CNAMERecord(final CNAMERecord cnameRecord) {
        super(cnameRecord.getName().clone(), cnameRecord.getType(), cnameRecord.getDnsClass(), cnameRecord.getTtl(),
                cnameRecord.getLength());
        singleName = cnameRecord.getSingleName().clone();
    }

    public CNAMERecord(final String domainName, final long ttl, final Name cname) {
        super(new Name(domainName), Type.CNAME, DNSClass.IN, ttl, cname.getLength());
        singleName = cname;
    }

    @Override
    protected void rrToByteResponse(final MessageOutput messageOutput) {
        singleName.toByteResponse(messageOutput);
    }

    @Override
    public Record clone() {
        return new CNAMERecord(this);
    }
}
