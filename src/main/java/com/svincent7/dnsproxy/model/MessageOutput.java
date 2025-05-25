package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class MessageOutput {
    private byte[] data;
    @Setter
    private int pos;

    private static final int BYTE_SIZE = 1;
    private static final int SHORT_BYTE_SIZE = BYTE_SIZE * 2;
    private static final int INT_BYTE_SIZE = SHORT_BYTE_SIZE * 2;

    private static final int UNSIGNED_BYTE_MASK = 0xFF;

    private static final int SHIFT_24 = 24;
    private static final int SHIFT_16 = 16;
    private static final int SHIFT_8 = 8;

    private static final int DEFAULT_MESSAGE_SIZE = 32;

    public MessageOutput(final int size) {
        data = new byte[size];
        pos = 0;
    }

    public MessageOutput() {
        this(DEFAULT_MESSAGE_SIZE);
    }

    public void writeU8(final int val) {
        ensureCapacity(BYTE_SIZE);
        data[pos++] = (byte) (val & UNSIGNED_BYTE_MASK);
    }

    public void writeU16(final int val) {
        ensureCapacity(SHORT_BYTE_SIZE);
        data[pos++] = (byte) ((val >>> SHIFT_8) & UNSIGNED_BYTE_MASK);
        data[pos++] = (byte) (val & UNSIGNED_BYTE_MASK);
    }

    public void writeU16At(final int val, final int where) {
        int offset = where;
        if (offset > pos - 2) {
            throw new IllegalArgumentException("cannot write past end of data");
        }
        data[offset++] = (byte) ((val >>> SHIFT_8) & UNSIGNED_BYTE_MASK);
        data[offset] = (byte) (val & UNSIGNED_BYTE_MASK);
    }

    public void writeU32(final long val) {
        ensureCapacity(INT_BYTE_SIZE);
        data[pos++] = (byte) ((val >>> SHIFT_24) & UNSIGNED_BYTE_MASK);
        data[pos++] = (byte) ((val >>> SHIFT_16) & UNSIGNED_BYTE_MASK);
        data[pos++] = (byte) ((val >>> SHIFT_8) & UNSIGNED_BYTE_MASK);
        data[pos++] = (byte) (val & UNSIGNED_BYTE_MASK);
    }

    public void writeByteArray(final byte[] b, final int off, final int len) {
        ensureCapacity(len);
        System.arraycopy(b, off, data, pos, len);
        pos += len;
    }

    public void writeByteArray(final byte[] b) {
        writeByteArray(b, 0, b.length);
    }

    public void writeCountedString(final byte[] s) {
        if (s.length > UNSIGNED_BYTE_MASK) {
            throw new IllegalArgumentException("Invalid counted string");
        }
        data[pos++] = (byte) (s.length & UNSIGNED_BYTE_MASK);
        writeByteArray(s, 0, s.length);
    }

    private void ensureCapacity(final int n) {
        if (data.length - pos >= n) {
            return;
        }
        int newsize = data.length * 2;
        if (newsize < pos + n) {
            newsize = pos + n;
        }
        byte[] arr = new byte[newsize];
        System.arraycopy(data, 0, arr, 0, pos);
        data = arr;
    }

    public byte[] toByteArray() {
        byte[] out = new byte[pos];
        System.arraycopy(data, 0, out, 0, pos);
        return out;
    }
}
