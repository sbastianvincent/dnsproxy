package com.svincent7.dnsproxy.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public enum DNSClass {
    IN(1),
    CH(3),
    CHAOS(3),
    HS(4),
    HESIOD(4),
    NONE(254),
    ANY(255);

    private final int value;
    DNSClass(final int value) {
        this.value = value;
    }

    private static final Map<Integer, DNSClass> LOOKUP = new HashMap<>();

    static {
        for (DNSClass t : DNSClass.values()) {
            LOOKUP.put(t.getValue(), t);
        }
    }

    public static DNSClass fromValue(final int value) {
        DNSClass result = LOOKUP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Unknown DClass: " + value);
        }
        return result;
    }
}
