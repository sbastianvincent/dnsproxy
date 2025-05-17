package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum DNSClass {
    IN((short) 1),
    CH((short) 3),
    CHAOS((short) 3),
    HS((short) 4),
    HESIOD((short) 4),
    NONE((short) 254),
    ANY((short) 255);

    private final short value;
    DNSClass(final short value) {
        this.value = value;
    }

    private static final Map<Short, DNSClass> LOOKUP = new HashMap<>();

    static {
        for (DNSClass t : DNSClass.values()) {
            LOOKUP.put(t.getValue(), t);
        }
    }

    public static DNSClass fromValue(final short value) {
        DNSClass result = LOOKUP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Unknown DClass: " + value);
        }
        return result;
    }
}
