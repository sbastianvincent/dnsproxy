package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.ToString;

import java.nio.charset.StandardCharsets;

@Getter
@ToString
public class Name implements Cloneable {
    private final String name;

    private static final int UNSIGNED_BYTE_MASK = 0xFF;
    private static final int POINTER_FLAG = 0xC0;
    private static final int POINTER_MASK = 0x3F;
    private static final int POINTER_SHIFT = 8;
    private static final int MAX_COMPRESSION_DEPTH = 10;

    public Name(final MessageInput messageInput) {
        StringBuilder domain = new StringBuilder();
        int lengthConsumed = readName(messageInput, messageInput.getPosition(), domain, 0);
        messageInput.setPosition(messageInput.getPosition() + lengthConsumed);
        this.name = domain.toString();
    }

    public Name(final Name name) {
        this.name = name.getName();
    }

    public Name(final String name) {
        this.name = name;
    }

    public void toByteResponse(final MessageOutput messageOutput) {
        String[] labels = name.split("\\.");
        for (String label : labels) {
            if (label.isEmpty()) {
                continue;
            }

            byte[] labelBytes = label.getBytes(StandardCharsets.UTF_8);
            messageOutput.writeU8((short) labelBytes.length);
            messageOutput.writeByteArray(labelBytes, 0, labelBytes.length);
        }
        messageOutput.writeU8(0); // end of name
    }

    @Override
    public Name clone() {
        return new Name(this);
    }

    private int readName(final MessageInput messageInput, final int startPos, final StringBuilder domain,
                         final int depth) {
        if (depth > MAX_COMPRESSION_DEPTH) {
            throw new RuntimeException("Too many compression pointers (possible loop)");
        }

        int pos = startPos;
        boolean jumped = false;
        int totalLength = 0;

        while (true) {
            int len = messageInput.getByteAt(pos) & UNSIGNED_BYTE_MASK;

            // pointer detection: top two bits 11
            if ((len & POINTER_FLAG) == POINTER_FLAG) {
                if (!jumped) {
                    totalLength += 2; // pointer is 2 bytes
                }
                int b2 = messageInput.getByteAt(pos + 1) & UNSIGNED_BYTE_MASK;
                int pointer = ((len & POINTER_MASK) << POINTER_SHIFT) | b2;

                if (pointer == pos) {
                    throw new RuntimeException("Invalid pointer to self");
                }

                // recursive call, but pointer bytes do not count towards lengthConsumed after first jump
                readName(messageInput, pointer, domain, depth + 1);
                return totalLength;
            }

            if (len == 0) {
                if (!jumped) {
                    totalLength++;
                }
                break;
            }

            if (!jumped) {
                totalLength += (len + 1);
            }

            pos++;
            for (int i = 0; i < len; i++) {
                domain.append((char) (messageInput.getByteAt(pos) & UNSIGNED_BYTE_MASK));
                pos++;
            }
            domain.append('.');
        }

        return totalLength;
    }
}
