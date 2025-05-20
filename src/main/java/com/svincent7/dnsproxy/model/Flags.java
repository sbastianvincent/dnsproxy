package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum Flags {
    QR((short) 0),
    AA((short) 5),
    TC((short) 6),
    RD((short) 7),
    AD((short) 10),
    CD((short) 11);

    private final short value;
    Flags(final short value) {
        this.value = value;
    }

    private static final Map<Short, Flags> LOOKUP = new HashMap<>();

    static {
        for (Flags t : Flags.values()) {
            LOOKUP.put(t.getValue(), t);
        }
    }

    public static Flags fromValue(final short value) {
        Flags result = LOOKUP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Unknown Flags: " + value);
        }
        return result;
    }
}
