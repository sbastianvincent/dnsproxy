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
    private final int transactionId;
    private int flags;
    private final int[] counts;

    public static final int SECTION_QUESTION = 0;
    public static final int SECTION_ANSWER = SECTION_QUESTION + 1;
    public static final int SECTION_AUTHORITY_RR = SECTION_ANSWER + 1;
    public static final int SECTION_ADDITIONAL_RR = SECTION_AUTHORITY_RR + 1;

    public static final int FLAGS_POSITION = 2;
    public static final int ADDITIONAL_POSITION = 10;

    private static final int RCODE_MASK = 0x000F;
    private static final int FLAGS_BIT_LENGTH = 15;

    public Header(final MessageInput messageInput) {
        this.transactionId = messageInput.readU16();
        this.flags = messageInput.readU16();
        this.counts = new int[Message.TOTAL_SECTION];
        for (int i = 0; i < counts.length; i++) {
            this.counts[i] = messageInput.readU16();
        }
    }

    public Header(final int transactionId, final int flags, final int[] counts) {
        this.transactionId = transactionId;
        this.flags = flags;
        this.counts = counts;
    }

    public void setRCode(final RCode rcode) {
        flags &= ~RCODE_MASK;
        flags |= (rcode.getValue() & RCODE_MASK);
    }

    public RCode getRCode() {
        return RCode.fromValue(flags & RCODE_MASK);
    }

    public void setFlag(final Flags flag) {
        flags |= (1 << (FLAGS_BIT_LENGTH - flag.getValue()));
    }

    public boolean isFlagSet(final Flags flag) {
        return (flags & (1 << (FLAGS_BIT_LENGTH - flag.getValue()))) != 0;
    }

    public boolean isTruncated() {
        return isFlagSet(Flags.TC);
    }

    public void setNxDomain() {
        setRCode(RCode.NXDOMAIN);
    }

    public void toByteResponse(final MessageOutput messageOutput) {
        messageOutput.writeU16(transactionId);
        messageOutput.writeU16(flags);
        for (int count: counts) {
            messageOutput.writeU16(count);
        }
    }

    public void incrementCount(final int section) {
        counts[section]++;
    }
}
