package com.svincent7.dnsproxy.model;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.model.records.RecordFactory;
import com.svincent7.dnsproxy.model.records.RecordFactoryImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Slf4j
@ToString
public class Message implements Cloneable {
    private final Header header;
    private final Map<Integer, List<Record>> sections;
    @Setter
    private boolean isReturnedFromCache = false;
    @Setter
    private boolean isDNSRewritten = false;

    public static final int TOTAL_SECTION = 4;

    public Message(final MessageInput messageInput) throws DNSMessageParseException {
        this.header = new Header(messageInput);
        this.sections = new ConcurrentHashMap<>();
        RecordFactory recordFactory = new RecordFactoryImpl();
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
        log.debug("{}: {}", this, header.getOpCode());
    }

    public Message(final Header header, final Map<Integer, List<Record>> sections) {
        this.header = header;
        this.sections = sections;
    }

    public Message(final Message message) {
        this.header = new Header(message.getHeader().getTransactionId(),
                message.getHeader().getFlags(), message.getHeader().getCounts().clone());

        this.sections = new HashMap<>();
        for (Map.Entry<Integer, List<Record>> entry : message.getSections().entrySet()) {
            List<Record> clonedList = new ArrayList<>();
            for (Record record : entry.getValue()) {
                clonedList.add(record.clone());
            }
            this.sections.put(entry.getKey(), clonedList);
        }
    }

    public boolean isQueryComplete() {
        return header.getRCode().equals(RCode.NOERROR) && isAllQuestionAnswered();
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

    public List<Record> getQuestionRecords() {
        if (!getSections().containsKey(Header.SECTION_QUESTION) || getSections().get(Header.SECTION_QUESTION)
                .isEmpty()) {
            return new ArrayList<>();
        }

        return getSections().get(Header.SECTION_QUESTION);
    }

    public List<Record> getAnswerRecords() {
        if (!getSections().containsKey(Header.SECTION_ANSWER) || getSections().get(Header.SECTION_ANSWER)
                .isEmpty()) {
            return new ArrayList<>();
        }

        return getSections().get(Header.SECTION_ANSWER);
    }

    public void addAnswerRecord(final Record record) {
        getSections().computeIfAbsent(Header.SECTION_ANSWER, k -> new ArrayList<>()).add(record);
        getHeader().getCounts()[Header.SECTION_ANSWER]++;
    }


    @Override
    public Message clone() {
        return new Message(this);
    }

    private boolean isAllQuestionAnswered() {
        return getQuestionRecords().stream().allMatch(
                q -> getAnswerRecords().stream().anyMatch(a -> a.getType().equals(q.getType()))
        );
    }

}
