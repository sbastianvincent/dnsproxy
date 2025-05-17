package com.svincent7.dnsproxy.model.records;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.MessageInput;

public interface RecordFactory {
    Record getRecordFromDnsMessage(MessageInput messageInput, int section) throws DNSMessageParseException;
}
