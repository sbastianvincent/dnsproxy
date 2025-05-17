package com.svincent7.dnsproxy.model;

import lombok.ToString;

import java.nio.ByteBuffer;

@ToString
public class MessageInput {
    private final ByteBuffer byteBuffer;

    private static final int UNSIGNED_BYTE_MASK = 0xFF;
    private static final long UNSIGNED_INT_MASK = 0xFFFFFFFFL;

    public MessageInput(final byte[] data) {
        this(ByteBuffer.wrap(data));
    }

    public MessageInput(final ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public int readU8() {
        return byteBuffer.get() & UNSIGNED_BYTE_MASK;
    }

    public short readU16() {
        return byteBuffer.getShort();
    }

    public long readU32() {
        return byteBuffer.getInt() & UNSIGNED_INT_MASK;
    }

    public byte[] readByteArray(final int len) {
        byte[] out = new byte[len];
        byteBuffer.get(out, 0, len);
        return out;
    }

    public int getPosition() {
        return byteBuffer.position();
    }

    public void setPosition(final int pos) {
        byteBuffer.position(pos);
    }

    public byte getByteAt(final int index) {
        return byteBuffer.get(index);
    }

    public int remaining() {
        return byteBuffer.remaining();
    }

    public byte[] readCountedString() {
        int len = readU8();
        return readByteArray(len);
    }
}
