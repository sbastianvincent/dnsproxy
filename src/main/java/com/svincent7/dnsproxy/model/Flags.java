package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum Flags {
    QR(0),
    AA(5),
    TC(6),
    RD(7),
    AD(10),
    CD(11);

    private final int value;
    Flags(final int value) {
        this.value = value;
    }

    private static final Map<Integer, Flags> LOOKUP = new HashMap<>();

    static {
        for (Flags t : Flags.values()) {
            LOOKUP.put(t.getValue(), t);
        }
    }

    public static Flags fromValue(final int value) {
        Flags result = LOOKUP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Unknown Flags: " + value);
        }
        return result;
    }
}
