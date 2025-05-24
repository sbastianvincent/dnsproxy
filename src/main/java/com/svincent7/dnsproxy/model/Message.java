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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ToString
@Slf4j
public class Message {
    private final Header header;
    private final Map<Integer, List<Record>> sections;
    @Setter
    @Getter
    private boolean isReturnedFromCache = false;
    @Setter
    @Getter
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
    }

    public boolean isQueryComplete() {
        return header.getRCode().equals(RCode.NOERROR) && isAllQuestionAnswered();
    }

    public boolean isBlockedResponse() {
        return header.getRCode() == RCode.NXDOMAIN || header.getRCode() == RCode.REFUSED;
    }

    public void toByteResponse(final MessageOutput messageOutput, final int maxPacketSize) {
        header.toByteResponse(messageOutput);
        int additional = header.getCounts()[Header.SECTION_ADDITIONAL_RR];
        for (int i = 0; i < TOTAL_SECTION; i++) {
            if (sections.get(i) == null) {
                continue;
            }
            for (Record record : sections.get(i)) {
                int recordSize = record.toByteResponse(messageOutput, maxPacketSize);
                if (messageOutput.getPos() + recordSize > maxPacketSize) {
                    if (i != Header.SECTION_ADDITIONAL_RR) {
                        log.debug("Skipping additional rr and adding flag");
                        header.setFlag(Flags.TC);
                        // re-write the header flag
                        messageOutput.writeU16At(header.getFlags(), Header.FLAGS_POSITION);
                    } else {
                        additional--;
                    }
                    break;
                }
            }
        }
        header.getCounts()[Header.SECTION_ADDITIONAL_RR] = (short) additional;
        messageOutput.writeU16At(header.getCounts()[Header.SECTION_ADDITIONAL_RR], Header.ADDITIONAL_POSITION);
    }

    public List<Record> getQuestionRecords() {
        if (!sections.containsKey(Header.SECTION_QUESTION) || sections.get(Header.SECTION_QUESTION)
                .isEmpty()) {
            return new ArrayList<>();
        }

        return sections.get(Header.SECTION_QUESTION);
    }

    public List<Record> getAnswerRecords() {
        if (!sections.containsKey(Header.SECTION_ANSWER) || sections.get(Header.SECTION_ANSWER)
                .isEmpty()) {
            return new ArrayList<>();
        }

        return sections.get(Header.SECTION_ANSWER);
    }

    public void addAnswerRecord(final Record record) {
        sections.computeIfAbsent(Header.SECTION_ANSWER, k -> new ArrayList<>()).add(record);
        header.incrementCount(Header.SECTION_ANSWER);
        header.setFlag(Flags.QR);
    }

    public void setBlocked() {
        header.setNxDomain();
        header.setFlag(Flags.QR);
    }

    public boolean isTruncated() {
        return header.isTruncated();
    }

    private boolean isAllQuestionAnswered() {
        return getQuestionRecords().stream().allMatch(
                q -> getAnswerRecords().stream().anyMatch(a -> a.getType().equals(q.getType()))
        );
    }

}
