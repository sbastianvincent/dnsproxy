package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.DNSClass;
import com.svincent7.dnsproxy.model.MessageInput;
import com.svincent7.dnsproxy.model.Header;
import com.svincent7.dnsproxy.model.Name;
import com.svincent7.dnsproxy.model.Type;
import lombok.ToString;

@ToString
public class RecordFactoryImpl implements RecordFactory {

    @Override
    public Record getRecordFromDnsMessage(final MessageInput messageInput, final int section)
            throws DNSMessageParseException {
        Name name = new Name(messageInput);
        Type type = Type.fromValue(messageInput.readU16());
        DNSClass dnsClass = DNSClass.fromValue(messageInput.readU16());
        if (section == Header.SECTION_QUESTION) {
            return new QRecord(name, type, dnsClass);
        }
        long ttl = messageInput.readU32();
        int length = messageInput.readU16();

        return switch (type) {
            case A -> new ARecord(name, type, dnsClass, ttl, length, messageInput);
            case SOA -> new SOARecord(name, type, dnsClass, ttl, length, messageInput);
            case AAAA -> new AAAARecord(name, type, dnsClass, ttl, length, messageInput);
            case CNAME -> new CNAMERecord(name, type, dnsClass, ttl, length, messageInput);
            case HTTPS -> new HTTPSRecord(name, type, dnsClass, ttl, length, messageInput);
            default -> throw new IllegalArgumentException("Unknown record type: " + type);
        };
    }
}
