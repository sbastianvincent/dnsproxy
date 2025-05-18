package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.ToString;

/**
 * Header contains 12 bytes.
 * aa aa 01 00 00 01 00 00 00 00 00 00
 * Transaction ID   aa aa  Random identifier for matching requests/responses
 * Flags            01 00  Standard query (0x0100)
 * Questions        00 01  One question
 * Answer RRs       00 00  Zero answers
 * Authority RRs    00 00  Zero authority records
 * Additional RRs   00 00  Zero additional records
 */
@Getter
@ToString
public class Header {
    private final short transactionId;
    private final short flags;
    private final short[] counts;

    public static final int SECTION_QUESTION = 0;
    public static final int SECTION_ANSWER = SECTION_QUESTION + 1;
    public static final int SECTION_AUTHORITY_RR = SECTION_ANSWER + 1;
    public static final int SECTION_ADDITIONAL_RR = SECTION_AUTHORITY_RR + 1;

    private static final int UNSIGNED_SHORT_MASK = 0xFFFF;
    private static final int OPCODE_MASK = 0xF;
    private static final int OPCODE_SHIFT = 11;
    private static final int RCODE_MASK = 0x000F;

    public Header(final MessageInput messageInput) {
        this.transactionId = messageInput.readU16();
        this.flags = messageInput.readU16();
        this.counts = new short[Message.TOTAL_SECTION];
        for (short i = 0; i < counts.length; i++) {
            this.counts[i] = messageInput.readU16();
        }
    }

    public Header(final short transactionId, final short flags, final short[] counts) {
        this.transactionId = transactionId;
        this.flags = flags;
        this.counts = counts;
    }

    public OpCode getOpCode() {
        return OpCode.fromValue((((flags & UNSIGNED_SHORT_MASK) >> OPCODE_SHIFT) & OPCODE_MASK));
    }

    public RCode getRCode() {
        return RCode.fromValue(flags & RCODE_MASK);
    }

    public short getTotalQuestions() {
        return counts[SECTION_QUESTION];
    }

    public short getTotalAnswers() {
        return counts[SECTION_ANSWER];
    }

    public void toByteResponse(final MessageOutput messageOutput) {
        messageOutput.writeU16(transactionId);
        messageOutput.writeU16(flags);
        for (short count: counts) {
            messageOutput.writeU16(count);
        }
    }
}
