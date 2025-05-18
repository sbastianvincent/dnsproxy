package com.svincent7.dnsproxy.model;

import com.svincent7.dnsproxy.exception.DNSMessageParseException;
import com.svincent7.dnsproxy.model.records.ARecord;
import com.svincent7.dnsproxy.model.records.Record;
import com.svincent7.dnsproxy.model.records.RecordFactory;
import com.svincent7.dnsproxy.model.records.RecordFactoryImpl;
import com.svincent7.dnsproxy.model.records.parametersvcb.ParameterIpv4Hint;
import com.svincent7.dnsproxy.service.dnsrewrites.DNSRewrites;
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
public class Message implements Cloneable {
    private final Header header;
    private final Map<Integer, List<Record>> sections;

    public static final int TOTAL_SECTION = 4;

    public Message(final MessageInput messageInput) throws DNSMessageParseException {
        this.header = new Header(messageInput);
        this.sections = new HashMap<>();
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
        return header.getRCode().equals(RCode.NOERROR) && header.getTotalQuestions() <= header.getTotalAnswers();
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

    public static Message fromDNSRewrites(final Message message, final DNSRewrites rewrites) {
        short[] counts = message.getHeader().getCounts().clone();
        counts[Header.SECTION_ANSWER] = 1;

        Header clonedHeader = new Header(message.getHeader().getTransactionId(),
                message.getHeader().getFlags(), counts);

        Record questionRecord = message.getSections()
                .get(Header.SECTION_QUESTION)
                .get(0);

        Map<Integer, List<Record>> clonedSections = new HashMap<>();

        // Add Question Section
        List<Record> questionRecords = new ArrayList<>();
        questionRecords.add(questionRecord);
        clonedSections.put(Header.SECTION_QUESTION, questionRecords);

        // Add Answer Section
        List<Record> answerList = new ArrayList<>();
        Record answerRecord = new ARecord(questionRecord.getName(), questionRecord.getType(),
                questionRecord.getDnsClass(), DNSRewrites.DEFAULT_REWRITE_TTL, ParameterIpv4Hint.IPV4_ADDRESS_LENGTH,
                rewrites.getAnswer());
        answerList.add(answerRecord);
        clonedSections.put(Header.SECTION_ANSWER, answerList);

        return new Message(clonedHeader, clonedSections);
    }

    public static Message fromCachedMessage(final Message requestMessage, final Message cachedMessage) {
        Header clonedHeader = new Header(requestMessage.getHeader().getTransactionId(),
                requestMessage.getHeader().getFlags(), requestMessage.getHeader().getCounts().clone());

        Map<Integer, List<Record>> clonedSections = new HashMap<>();
        for (Map.Entry<Integer, List<Record>> entry : cachedMessage.getSections().entrySet()) {
            List<Record> clonedList = new ArrayList<>();
            for (Record record : entry.getValue()) {
                clonedList.add(record.clone());
            }
            clonedSections.put(entry.getKey(), clonedList);
        }

        return new Message(clonedHeader, clonedSections);
    }

    @Override
    public Message clone() {
        return new Message(this);
    }
}
