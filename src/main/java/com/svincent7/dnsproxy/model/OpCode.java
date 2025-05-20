package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * https://www.rfc-editor.org/rfc/rfc5395.txt.
 * OpCode Name                               Reference
 * 0     Query                              [RFC1035]
 * 1     IQuery (Inverse Query, Obsolete)   [RFC3425]
 * 2     Status                             [RFC1035]
 * 3     available for assignment
 * 4     Notify                             [RFC1996]
 * 5     Update                             [RFC2136]
 * 6-15   available for assignment
 */
@Getter
@ToString
public enum OpCode {
    QUERY(0),
    IQUERY(1),
    STATUS(2),
    NOTIFY(4),
    UPDATE(5);

    private final int value;
    OpCode(final int value) {
        this.value = value;
    }

    private static final Map<Integer, OpCode> LOOKUP = new HashMap<>();

    static {
        for (OpCode t : OpCode.values()) {
            LOOKUP.put(t.getValue(), t);
        }
    }

    public static OpCode fromValue(final int value) {
        OpCode result = LOOKUP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Unknown OpCode: " + value);
        }
        return result;
    }
}
