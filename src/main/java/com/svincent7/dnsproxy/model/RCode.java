package com.svincent7.dnsproxy.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum RCode {
    NOERROR(0),
    FORMERR(1),
    SERVFAIL(2),
    NXDOMAIN(3),
    NOTIMP(4),
    REFUSED(5);

    private final int value;
    RCode(final int value) {
        this.value = value;
    }

    private static final Map<Integer, RCode> LOOKUP = new HashMap<>();

    static {
        for (RCode t : RCode.values()) {
            LOOKUP.put(t.getValue(), t);
        }
    }

    public static RCode fromValue(final int value) {
        RCode result = LOOKUP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Unknown RCode: " + value);
        }
        return result;
    }
}
