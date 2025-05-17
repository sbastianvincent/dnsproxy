package com.svincent7.dnsproxy.model;

import com.svincent7.dnsproxy.model.records.RecordFactory;
import com.svincent7.dnsproxy.model.records.RecordFactoryImpl;
import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.records.Record;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
@ToString
public class Message {
    private final Header header;
    private final RecordFactory recordFactory;
    private final Map<Integer, List<Record>> sections;

    public static final int TOTAL_SECTION = 4;

    public Message(final MessageInput messageInput) throws DNSMessageParseException {
        this.header = new Header(messageInput);
        this.sections = new HashMap<>();
        this.recordFactory = new RecordFactoryImpl();
        for (int i = 0; i < TOTAL_SECTION; i++) {
            List<Record> list = new ArrayList<>();
            int count = header.getCounts()[i];
            if (count == 0) {
                continue;
            }
            for (int j = 0; j < count; j++) {
                list.add(recordFactory.getRecordFromDnsMessage(messageInput, i));
            }
            sections.put(i, list);
        }
        log.info("{}: {}", this, header.getOpCode());
    }

    public boolean isQueryComplete() {
        return header.getTotalQuestions() == header.getTotalAnswers();
    }

    public void toByteResponse(final MessageOutput messageOutput) {
        header.toByteResponse(messageOutput);
        for (int i = 0; i < TOTAL_SECTION; i++) {
            if (sections.get(i) == null) {
                continue;
            }
            for (Record record : sections.get(i)) {
                record.toByteResponse(messageOutput);
            }
        }
    }
}
